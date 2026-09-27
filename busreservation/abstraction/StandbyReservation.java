package busreservation.abstraction;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.CapacityException;
import busreservation.model.ReservationResult;
import busreservation.model.ReservationStatus;

public class StandbyReservation extends SeatReservationService {
    public StandbyReservation(CapacityTrackingSystem capacitySystem, int busCapacity) {
        super(capacitySystem, busCapacity);
    }
    @Override
    public ReservationResult reserve(String busId, String passengerId) {
        try {
            int reserved = capacitySystem.getReservedCount(busId);
            if (reserved < busCapacity) {
                capacitySystem.recordReservation(busId, passengerId);
                return new ReservationResult(
                        ReservationStatus.CONFIRMED_PRIMARY, busId, "Standby seat granted on " + busId);
            }
            return new ReservationResult(
                    ReservationStatus.FULL, busId, "No standby seats left on " + busId);
        } catch (CapacityException e) {
            return new ReservationResult(
                    ReservationStatus.UNKNOWN, busId, "Capacity system error: " + e.getMessage());
        }
    }
}
