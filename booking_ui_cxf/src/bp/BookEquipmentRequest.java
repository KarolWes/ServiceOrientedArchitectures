
package bp;


import bp.types.Equipment;
import bp.types.Person;
import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "person",
    "equipment", 
        "startDate"
})
@XmlRootElement(name = "bookEquipmentRequest")
@Data
public class BookEquipmentRequest {

    @XmlElement(required = true)
    protected String person;
    @XmlElement(required = true)
    protected String equipment;
    @XmlElement(required = true)
    protected LocalDate startDate;

}
