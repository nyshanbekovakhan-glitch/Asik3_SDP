package busreservation.implementor;

import busreservation.model.CapacityException;
public interface CapacityTrackingSystem {
    int getReservedCount(String busId) throws CapacityException;
    void recordReservation(String busId, String passengerId) throws CapacityException;
}
