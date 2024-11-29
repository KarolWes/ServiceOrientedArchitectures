package lab.aznu.equipment_booking.types;

public class BookingError extends RuntimeException {
    public BookingError(String message) {
        super(message);
    }
}
