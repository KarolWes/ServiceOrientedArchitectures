package lab.aznu.equipment_booking.booking_func;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@XmlRootElement(name = "BookEquipmentResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookEquipmentResponse {

    @XmlElement(name = "bookingId")
    private int bookingId;

    @XmlElement(name = "price")
    private double price;

    @XmlElement(name="returnDate")
    private LocalDate returnDate;

}
