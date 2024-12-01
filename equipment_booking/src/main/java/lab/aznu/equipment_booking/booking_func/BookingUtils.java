package lab.aznu.equipment_booking.booking_func;

import lab.aznu.equipment_booking.types.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class BookingUtils {
    private static final double HARNESS_PRICE = 100.00;
    private static final double DEFAULT_PRICE = 50.00;
    private static final double LATE_RETURN_FEE = 5.00;
    private static int bookingIdCounter = 1;

    public BookingInfo bookEquipment(String  client, String equipment, LocalDate startDate)
            throws BookingError {
        validateBookingInput(client, equipment, startDate);

        Equipment eq = new Equipment(Integer.parseInt(equipment));
        if (!eq.getState().equals(BookingState.FREE)) {
            throw new BookingError("Equipment is not available for booking.");
        }

        BookingInfo bookingInfo = new BookingInfo();
        bookingInfo.setId(generateBookingId());
        bookingInfo.setPerson(new Person(client));

        bookingInfo.setEquipment(eq);
        bookingInfo.setStartDate(startDate);
        bookingInfo.setEndDate(startDate.plusDays(30));
        eq.setState(BookingState.BOOKED);

        bookingInfo.setPrice(calculatePrice(eq));
        return bookingInfo;
    }

    public double returnEquipment(BookingInfo bookingInfo) throws BookingError {
        validateBookingInfo(bookingInfo);

        Equipment equipment = bookingInfo.getEquipment();
        equipment.setState(BookingState.FREE);

        long daysOverdue = Period.between(bookingInfo.getEndDate(), LocalDate.now()).getDays();
        double lateFee = daysOverdue > 0 ? daysOverdue * LATE_RETURN_FEE : 0;

        return bookingInfo.getPrice() + lateFee;
    }

    public double cancelBooking(BookingInfo bookingInfo) throws BookingError {
        validateBookingInfo(bookingInfo);

        Equipment equipment = bookingInfo.getEquipment();
        equipment.setState(BookingState.FREE);

        if (bookingInfo.getStartDate().isBefore(LocalDate.now())) {
            return bookingInfo.getPrice(); // Full charge if cancellation happens after the start date
        } else {
            return 0; // No charge for cancellation before the start date
        }
    }

    // Helper Methods
    private void validateBookingInput(String client, String equipment, LocalDate startDate) throws BookingError {
        if (client == null || equipment == null || startDate == null) {
            throw new BookingError("Booking details are incomplete.");
        }
    }

    private void validateBookingInfo(BookingInfo bookingInfo) throws BookingError {
        if (bookingInfo == null || bookingInfo.getEquipment() == null) {
            throw new BookingError("Invalid booking information.");
        }
    }

    private double calculatePrice(Equipment equipment) {
        return equipment.getType().equals("harness") ? HARNESS_PRICE : DEFAULT_PRICE;
    }

    private synchronized int generateBookingId() {
        return bookingIdCounter++;
    }
}






















