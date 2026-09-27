package busreservation.implementor;

import busreservation.model.CapacityException;
import java.util.HashMap;
import java.util.Map;

public class MobileBookingSystem implements CapacityTrackingSystem {
    private final Map<String, Integer> reservedCounts = new HashMap<>();

    @Override
    public int getReservedCount(String busId) throws CapacityException {
        return reservedCounts.getOrDefault(busId, 0);
    }

    @Override
    public void recordReservation(String busId, String passengerId) throws CapacityException {
        reservedCounts.merge(busId, 1, Integer::sum);
    }
}
