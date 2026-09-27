package busreservation.adapter;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.legacy.LegacyTicketMachine;
import busreservation.model.CapacityException;

public class LegacyTicketMachineAdapter implements CapacityTrackingSystem {

    private static final int BUS_CAPACITY = 40;
    private static final int SENSOR_FAULT_SENTINEL = 255;
    private static final int PUNCH_SUCCESS = 0;
    private static final int PUNCH_NO_SEATS = 1;
    private static final int PUNCH_DEVICE_FAULT = 2;

    private final LegacyTicketMachine legacyMachine;
    public LegacyTicketMachineAdapter(LegacyTicketMachine legacyMachine) {
        this.legacyMachine = legacyMachine;
    }

    @Override
    public int getReservedCount(String busId) throws CapacityException {
        int machineSerial = parseMachineSerial(busId);
        int remaining = legacyMachine.readRemainingTickets(machineSerial) & 0xFF;
        if (remaining == SENSOR_FAULT_SENTINEL) {
            throw new CapacityException(
                    "Legacy ticket machine " + machineSerial + " returned fault sentinel", null);
        }
        return BUS_CAPACITY - remaining;
    }

    @Override
    public void recordReservation(String busId, String passengerId) throws CapacityException {
        int machineSerial = parseMachineSerial(busId);
        int resultCode = legacyMachine.punchTicket(machineSerial);
        switch (resultCode) {
            case PUNCH_SUCCESS -> { }
            case PUNCH_NO_SEATS -> throw new CapacityException(
                    "Legacy machine " + machineSerial + " reports no seats available", null);
            case PUNCH_DEVICE_FAULT -> throw new CapacityException(
                    "Legacy machine " + machineSerial + " reports device fault", null);
            default -> throw new CapacityException(
                    "Legacy machine " + machineSerial + " returned unknown code " + resultCode, null);
        }
    }

    private int parseMachineSerial(String busId) {
        try {
            String[] parts = busId.split("-");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "busId '" + busId + "' does not map to a known ticket machine", e);
        }
    }
}