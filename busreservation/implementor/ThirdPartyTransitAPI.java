package busreservation.implementor;

import busreservation.model.CapacityException;

public class ThirdPartyTransitAPI implements CapacityTrackingSystem {

    @Override
    public int getReservedCount(String busId) throws CapacityException {
        try {
            return queryExternalCount(busId);
        } catch (TransitApiException e) {
            throw new CapacityException("Third-party transit API unreachable for " + busId, e);
        }
    }

    @Override
    public void recordReservation(String busId, String passengerId) throws CapacityException {
        try {
            submitExternalReservation(busId, passengerId);
        } catch (TransitApiException e) {
            throw new CapacityException("Third-party transit API failed to record for " + busId, e);
        }
    }

    private int queryExternalCount(String busId) throws TransitApiException {
        return 0;
    }
    private void submitExternalReservation(String busId, String passengerId) throws TransitApiException {
    }
}
