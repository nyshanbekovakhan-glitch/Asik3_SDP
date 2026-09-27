package busreservation.abstraction;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.CapacityException;
import busreservation.model.ReservationResult;
import busreservation.model.ReservationStatus;

public class MorningCommuteReservation extends SeatReservationService {
    private final BackupBusDispatcher backupDispatcher;
    public MorningCommuteReservation(CapacityTrackingSystem capacitySystem, int busCapacity,
                                     BackupBusDispatcher backupDispatcher) {
        super(capacitySystem, busCapacity);
        this.backupDispatcher = backupDispatcher;
    }

    @Override
    public ReservationResult reserve(String busId, String passengerId) {
        try {
            int reserved = capacitySystem.getReservedCount(busId);
            if (reserved < busCapacity) {
                capacitySystem.recordReservation(busId, passengerId);
                return new ReservationResult(
                        ReservationStatus.CONFIRMED_PRIMARY, busId, "Reserved seat on " + busId);
            }
            return backupDispatcher.dispatch(busId, passengerId);
        } catch (CapacityException e) {
            return new ReservationResult(
                    ReservationStatus.UNKNOWN, busId, "Capacity system error: " + e.getMessage());
        }
    }
}
