package busreservation.legacy;

public class LegacyTicketMachine {

    private static final int BUS_CAPACITY = 40;
    public byte readRemainingTickets(int machineSerial) {
        return (byte) BUS_CAPACITY;
    }
    public int punchTicket(int machineSerial) {
        return 0;
    }
}
