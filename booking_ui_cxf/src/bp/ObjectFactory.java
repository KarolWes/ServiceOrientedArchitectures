
package bp;


import bp.types.BookingError;
import bp.types.BookingInfo;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName BOOK_EQUIPMENT_RESPONSE = new QName("http://www.bp.org", "bookEquipmentResponse");
    private final static QName CANCEL_BOOKING_RESPONSE = new QName("http://www.bp.org", "cancelBookingResponse");
    private final static QName BOOKING_ERROR = new QName("http://www.bp.org", "bookingError");

    public ObjectFactory() {
    }

    public BookEquipmentRequest createBookEquipmentRequest() {
        return new BookEquipmentRequest();
    }

    public CancelBookingRequest createCancelBookingRequest() {
        return new CancelBookingRequest();
    }

    @XmlElementDecl(namespace = "http://www.bp.org", name = "bookEquipmentResponse")
    public JAXBElement<BookingInfo> createBookEquipmentResponse(BookingInfo value) {
        return new JAXBElement<BookingInfo>(BOOK_EQUIPMENT_RESPONSE, BookingInfo.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.bp.org", name = "cancelBookingResponse")
    public JAXBElement<BookingInfo> createCancelBookingResponse(BookingInfo value) {
        return new JAXBElement<BookingInfo>(CANCEL_BOOKING_RESPONSE, BookingInfo.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.bp.org", name = "bookingError")
    public JAXBElement<BookingError> createBookingError(BookingError value) {
        return new JAXBElement<BookingError>(BOOKING_ERROR, BookingError.class, null, value);
    }

}
