# Design Rationale — Bus Route Advance Reservation & Capacity Management System

## 1. Problem Chosen

Passengers pre-register for a time slot on a bus route so morning commutes do not become overcrowded. If the number of registered passengers reaches the route capacity, a backup bus is dispatched automatically for passengers who registered in advance. Passengers who arrive without registration are served as standby passengers and are not guaranteed a backup bus.

The system must work with different capacity-tracking technologies. A route may use a modern mobile booking system, a third-party transit API, or an old legacy ticket machine. The legacy ticket machine was created before modern software integration and cannot be modified.

The main abstraction is a reservation and capacity-management service. The system does not model different types of buses, so it does not use a vehicle hierarchy such as the Vehicle and Workshop example from the lectures.

## 2. Why Bridge Alone Would Not Be Enough

If only the Bridge pattern were used, the legacy ticket machine would need to implement the `CapacityTrackingSystem` interface directly.

However, `LegacyTicketMachine` cannot do this because it has a completely different interface. The target interface uses:

`getReservedCount(String busId): int`

`recordReservation(String busId, String passengerId): void`

The legacy system uses:

`readRemainingTickets(int machineSerial): byte`

`punchTicket(int machineSerial): int`

The legacy system uses a numeric machine identifier instead of a bus ID, does not store passenger identity, returns remaining tickets instead of reserved tickets, and uses status codes instead of exceptions.

Bridge allows the reservation abstraction and capacity implementations to vary independently, but it cannot make an incompatible legacy class fit the required interface. Therefore, an Adapter is also required.

## 3. Why Adapter Alone Would Not Be Enough

If only the Adapter pattern were used, the reservation logic could become one large class containing different conditions for different reservation policies and capacity systems.

For example, the class could contain separate logic for registered passengers and standby passengers. This would make the class harder to maintain and would mix reservation policies with capacity-tracking technologies.

The Adapter solves the problem of connecting the incompatible legacy ticket machine to the system. However, it does not allow different reservation policies to vary independently from the capacity-tracking technologies.

The Bridge pattern solves this second problem. `MorningCommuteReservation` and `StandbyReservation` can use the same `CapacityTrackingSystem` interface while implementing different reservation policies.

Therefore, Bridge and Adapter are combined because they solve two different but connected problems in the same system.

## 4. Why the Wrapped Implementation Is Genuinely Incompatible

The `CapacityTrackingSystem` interface and `LegacyTicketMachine` use different concepts and data formats.

| Aspect             | `CapacityTrackingSystem` | `LegacyTicketMachine`           |
| ------------------ | ------------------------ | ------------------------------- |
| Identifier         | `String busId`           | `int machineSerial`             |
| Passenger identity | `String passengerId`     | Not represented                 |
| Query result       | Reserved count as `int`  | Remaining tickets as `byte`     |
| Failure signal     | `CapacityException`      | Sentinel value and status codes |

The `LegacyTicketMachineAdapter` performs several real transformations.

First, it converts the route-style bus ID into the numeric machine serial expected by the legacy machine.

Second, it handles the legacy `byte` value and checks the sentinel value correctly.

Third, it converts the remaining-ticket count into the reserved-ticket count using the route capacity.

Fourth, it converts the legacy status codes into the common `CapacityException` used by the rest of the system.

Therefore, the Adapter does more than simply rename methods. It translates different identifiers, data types, meanings, and failure mechanisms.

## 5. Failure Translation and Open/Closed Principle

All three `CapacityTrackingSystem` implementations provide a common interface to the reservation layer. Internal failures are translated into `CapacityException`, so callers do not need to understand legacy status codes or implementation-specific errors.

The reservation abstractions depend only on the `CapacityTrackingSystem` interface. They do not directly reference `LegacyTicketMachine` or its implementation-specific errors.

A new reservation policy can be added as another `SeatReservationService` subclass without changing the existing capacity implementations.

A new capacity implementation can also be added behind the `CapacityTrackingSystem` interface without changing the reservation abstractions.

The `ImplementorSelector` is responsible for selecting the appropriate implementation for a route based on its configuration.

## 6. Complexity Module: Dynamic Implementor Selection

The system uses Dynamic Implementor Selection.

The concrete `CapacityTrackingSystem` implementation is selected at runtime by `ImplementorSelector`. The selection is based on `RouteConfig.bookingSystemType()`.

For example, a route configured with the `legacy` booking system receives a `LegacyTicketMachineAdapter`. The client does not directly create or reference the legacy adapter.

This allows the same reservation abstraction to work with different capacity-tracking technologies depending on the route configuration.

## 7. Why BackupBusDispatcher Is Outside the Implementor Hierarchy

`BackupBusDispatcher` is a separate collaborator used by `MorningCommuteReservation`.

It is not a capacity-tracking implementation because its responsibility is to dispatch a backup bus rather than track reservations or capacity.

It is also specific to the morning reservation policy. `StandbyReservation` does not dispatch a backup bus.

Keeping `BackupBusDispatcher` separate prevents unrelated responsibilities from being placed inside `SeatReservationService`. It also allows the reservation policy and capacity-tracking implementation to remain separated.

## 8. Limitation of the Final Design

The backup bus identifier is generated by adding `-BACKUP` to the primary bus ID.

For example:

`R12-7` becomes `R12-7-BACKUP`.

The `LegacyTicketMachineAdapter` expects a bus ID in the `<route>-<machineSerial>` format. Therefore, this generated backup ID cannot be interpreted by the legacy adapter.

In the current design, this means backup buses can be tracked through modern implementations but not through the legacy ticket machine.

This is a limitation of the current identifier convention. If backup buses also needed to operate with legacy ticket machines, the identifier mapping would need to be redesigned.
