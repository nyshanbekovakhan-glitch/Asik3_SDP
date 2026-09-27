package busreservation.selector;

import busreservation.adapter.LegacyTicketMachineAdapter;
import busreservation.implementor.CapacityTrackingSystem;
import busreservation.implementor.MobileBookingSystem;
import busreservation.implementor.ThirdPartyTransitAPI;
import busreservation.legacy.LegacyTicketMachine;
import busreservation.model.RouteConfig;

public class ImplementorSelector {
    public static CapacityTrackingSystem selectFor(RouteConfig route) {
        return select(route.bookingSystemType());
    }
    public static CapacityTrackingSystem select(String bookingSystemType) {
        return switch (bookingSystemType.toLowerCase()) {
            case "mobile" -> new MobileBookingSystem();
            case "thirdparty" -> new ThirdPartyTransitAPI();
            case "legacy" -> new LegacyTicketMachineAdapter(new LegacyTicketMachine());
            default -> throw new IllegalArgumentException(
                    "Unknown booking system type: " + bookingSystemType);
        };
    }
    private ImplementorSelector() {
    }
}
