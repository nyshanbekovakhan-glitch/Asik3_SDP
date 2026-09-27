package busreservation.abstraction;

import org.junit.jupiter.api.Test;
import busreservation.model.ReservationResult;
import busreservation.model.ReservationStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MorningReservationTest {
    @Test
    void confirmsPrimarySeatWhenCapacityAvailable() {
        StubCapacitySystem stub = new StubCapacitySystem();
        stub.setReservedCount("R12-7", 0);
        BackupBusDispatcher dispatcher = new BackupBusDispatcher(stub);
        MorningCommuteReservation service = new MorningCommuteReservation(stub, 2, dispatcher);
        ReservationResult result = service.reserve("R12-7", "passenger-1");
        assertEquals(ReservationStatus.CONFIRMED_PRIMARY, result.status());
        assertEquals("R12-7", result.busId());
        assertEquals(1, stub.recordCallCount);
    }
    @Test
    void dispatchesToBackupBusWhenPrimaryIsFull() {
        StubCapacitySystem stub = new StubCapacitySystem();
        stub.setReservedCount("R12-7", 2);
        BackupBusDispatcher dispatcher = new BackupBusDispatcher(stub);
        MorningCommuteReservation service = new MorningCommuteReservation(stub, 2, dispatcher);
        ReservationResult result = service.reserve("R12-7", "passenger-3");
        assertEquals(ReservationStatus.CONFIRMED_BACKUP, result.status());
        assertEquals("R12-7-BACKUP", result.busId());
    }
}
