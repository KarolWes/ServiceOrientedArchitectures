
package bp;


import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "cancelBookingRequest")
@Data
public class CancelBookingRequest {

    @XmlAttribute(name = "bookingId")
    protected Integer bookingId;

}
