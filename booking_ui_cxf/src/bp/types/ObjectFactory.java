
package bp.types;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }


    public Person createPerson() {
        return new Person();
    }


    public Equipment createEquipment() {
        return new Equipment();
    }


    public BookingInfo createBookingInfo() {
        return new BookingInfo();
    }


    public BookingError createError() {
        return new BookingError();
    }




}
