package busreservation;

import busreservation.abstraction.BackupBusDispatcher;
import busreservation.abstraction.MorningCommuteReservation;
import busreservation.abstraction.StandbyReservation;
import busreservation.implementor.CapacityTrackingSystem;
import busreservation.model.RouteConfig;
import busreservation.selector.ImplementorSelector;

public class Main {

    public static void main(String[] args) {

        RouteConfig oldRoute = new RouteConfig("R12-7", "legacy");
        CapacityTrackingSystem capacitySystem = ImplementorSelector.selectFor(oldRoute);

        BackupBusDispatcher dispatcher = new BackupBusDispatcher(capacitySystem);
        MorningCommuteReservation commute =
                new MorningCommuteReservation(capacitySystem, 2, dispatcher);

        System.out.println(commute.reserve("R12-7", "passenger-1"));
        System.out.println(commute.reserve("R12-7", "passenger-2"));
        System.out.println(commute.reserve("R12-7", "passenger-3"));

        StandbyReservation standby = new StandbyReservation(capacitySystem, 2);
        System.out.println(standby.reserve("R12-7", "walkup-1"));
    }
}