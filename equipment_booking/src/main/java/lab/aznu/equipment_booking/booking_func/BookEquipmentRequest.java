package lab.aznu.equipment_booking.booking_func;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "BookEquipmentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookEquipmentRequest {

    // Getters and Setters
    @XmlElement(name = "client", required = true)
    private String client;

    @XmlElement(name = "equipmentId", required = true)
    private int equipmentId;

    @XmlElement(name = "startDate", required = true)
    private String startDate;

}

