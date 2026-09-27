package busreservation.adapter;

import org.junit.jupiter.api.Test;
import busreservation.legacy.LegacyTicketMachine;
import busreservation.model.CapacityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegacyAdapterTest {
    @Test
    void computesReservedCountAsCapacityMinusRemaining() throws CapacityException {
        LegacyTicketMachine fakeMachine = new LegacyTicketMachine() {
            @Override
            public byte readRemainingTickets(int machineSerial) {
                return 30;
            }
        };
        LegacyTicketMachineAdapter adapter = new LegacyTicketMachineAdapter(fakeMachine);
        assertEquals(10, adapter.getReservedCount("R12-7"));
    }
    @Test
    void translatesFaultSentinelToCapacityException() {
        LegacyTicketMachine fakeMachine = new LegacyTicketMachine() {
            @Override
            public byte readRemainingTickets(int machineSerial) {
                return (byte) 255;
            }
        };
        LegacyTicketMachineAdapter adapter = new LegacyTicketMachineAdapter(fakeMachine);
        assertThrows(CapacityException.class, () -> adapter.getReservedCount("R12-7"));
    }
    @Test
    void translatesNoSeatsCodeToCapacityException() {
        LegacyTicketMachine fakeMachine = new LegacyTicketMachine() {
            @Override
            public int punchTicket(int machineSerial) {
                return 1;
            }
        };
        LegacyTicketMachineAdapter adapter = new LegacyTicketMachineAdapter(fakeMachine);
        assertThrows(CapacityException.class,
                () -> adapter.recordReservation("R12-7", "passenger-1"));
    }
    @Test
    void rejectsBusIdThatDoesNotMapToMachine() {
        LegacyTicketMachineAdapter adapter = new LegacyTicketMachineAdapter(new LegacyTicketMachine());

        assertThrows(IllegalArgumentException.class,
                () -> adapter.getReservedCount("no-machine-here"));
    }
}