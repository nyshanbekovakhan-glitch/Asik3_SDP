package busreservation.abstraction;

import org.junit.jupiter.api.Test;
import busreservation.model.ReservationResult;
import busreservation.model.ReservationStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StandbyTest {
    @Test
    void confirmsSeatWhenCapacityAvailable() {
        StubCapacitySystem stub = new StubCapacitySystem();
        stub.setReservedCount("R12-7", 0);
        StandbyReservation service = new StandbyReservation(stub, 2);
        ReservationResult result = service.reserve("R12-7", "walkup-1");
        assertEquals(ReservationStatus.CONFIRMED_PRIMARY, result.status());
    }
    @Test
    void returnsFullWithoutBackupWhenPrimaryIsFull() {
        StubCapacitySystem stub = new StubCapacitySystem();
        stub.setReservedCount("R12-7", 2);
        StandbyReservation service = new StandbyReservation(stub, 2);
        ReservationResult result = service.reserve("R12-7", "walkup-1");
        assertEquals(ReservationStatus.FULL, result.status());
        assertEquals("R12-7", result.busId());
    }
}
