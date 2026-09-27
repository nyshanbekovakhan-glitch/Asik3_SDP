package busreservation.abstraction;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.CapacityException;
import busreservation.model.ReservationResult;
import busreservation.model.ReservationStatus;

public class BackupBusDispatcher {

    private static final int BACKUP_BUS_CAPACITY = 40;
    private static final String BACKUP_SUFFIX = "-BACKUP";
    private final CapacityTrackingSystem capacitySystem;
    public BackupBusDispatcher(CapacityTrackingSystem capacitySystem) {
        this.capacitySystem = capacitySystem;
    }
    public ReservationResult dispatch(String primaryBusId, String passengerId) throws CapacityException {
        String backupBusId = primaryBusId + BACKUP_SUFFIX;
        int reserved = capacitySystem.getReservedCount(backupBusId);
        if (reserved >= BACKUP_BUS_CAPACITY) {
            return new ReservationResult(
                    ReservationStatus.FULL, backupBusId, "Backup bus is also full for " + primaryBusId);
        }
        capacitySystem.recordReservation(backupBusId, passengerId);
        return new ReservationResult(
                ReservationStatus.CONFIRMED_BACKUP, backupBusId, "Backup bus dispatched for " + primaryBusId);
    }
}
