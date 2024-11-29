package lab.aznu.equipment_booking.types;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingInfo {
    private int id;
    private Person person;
    private Equipment equipment;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price = 0;

}
