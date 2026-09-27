package busreservation.abstraction;

import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.CapacityException;
import java.util.HashMap;
import java.util.Map;

public class StubCapacitySystem implements CapacityTrackingSystem {

    private final Map<String, Integer> counts = new HashMap<>();
    private final Map<String, CapacityException> failures = new HashMap<>();
    public int recordCallCount = 0;
    public void setReservedCount(String busId, int count) {
        counts.put(busId, count);
    }
    public void failOn(String busId, CapacityException ex) {
        failures.put(busId, ex);
    }
    @Override
    public int getReservedCount(String busId) throws CapacityException {
        if (failures.containsKey(busId)) {
            throw failures.get(busId);
        }
        return counts.getOrDefault(busId, 0);
    }
    @Override
    public void recordReservation(String busId, String passengerId) throws CapacityException {
        recordCallCount++;
        counts.merge(busId, 1, Integer::sum);
    }
}