package busreservation.model;

public record ReservationResult(ReservationStatus status, String busId, String message) {
}
