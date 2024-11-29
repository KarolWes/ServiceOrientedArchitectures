package lab.aznu.equipment_booking.booking_func;

import lab.aznu.equipment_booking.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.LocalDate;

@Endpoint
public class BookingEndpoint {

    private static final String NAMESPACE_URI = "";


    private final BookingUtils bookingService;

    public BookingEndpoint(BookingUtils bookingService) {
        this.bookingService = bookingService;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "BookEquipmentRequest")
    @ResponsePayload
    public BookEquipmentResponse bookEquipment(@RequestPayload BookEquipmentRequest request) {
        try {
            BookingInfo bookingInfo = bookingService.bookEquipment(
                    new Person(request.getClient()),
                    new Equipment(request.getEquipmentId()),
                    LocalDate.parse(request.getStartDate())
            );

            BookEquipmentResponse response = new BookEquipmentResponse();
            response.setBookingId(bookingInfo.getId());
            response.setPrice(bookingInfo.getPrice());
            response.setReturnDate(bookingInfo.getEndDate());
            return response;

        } catch (BookingError e) {
            throw new RuntimeException("Booking failed: " + e.getMessage(), e);
        }
    }
}

