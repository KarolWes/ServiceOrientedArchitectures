
package bp.equipment;

import bp.types.BookingError;
import jakarta.xml.ws.WebFault;

@WebFault(name = "bookingError", targetNamespace = "http://www.bp.org")
public class BookingErrorMsg extends Exception {

    private BookingError bookingError;

    public BookingErrorMsg() {
        super();
    }

    public BookingErrorMsg(String message) {
        super(message);
    }

    public BookingErrorMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingErrorMsg(String message, BookingError bookingError) {
        super(message);
        this.bookingError = bookingError;
    }

    public BookingErrorMsg(String message, BookingError bookingError, Throwable cause) {
        super(message, cause);
        this.bookingError = bookingError;
    }

    public BookingError getBookingErrorInfo() {
        return this.bookingError;
    }
}
