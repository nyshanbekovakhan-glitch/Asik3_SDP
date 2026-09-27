package busreservation.abstraction;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.ReservationResult;

public abstract class SeatReservationService {
    protected final CapacityTrackingSystem capacitySystem;
    protected final int busCapacity;
    protected SeatReservationService(CapacityTrackingSystem capacitySystem, int busCapacity) {
        this.capacitySystem = capacitySystem;
        this.busCapacity = busCapacity;
    }
    public abstract ReservationResult reserve(String busId, String passengerId);
}
