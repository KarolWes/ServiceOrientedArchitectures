package bp.equipment;


import bp.BookEquipmentRequest;
import bp.CancelBookingRequest;
import bp.types.BookingInfo;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import bp.types.ObjectFactory;

@WebService(targetNamespace = "http://www.bp.org/equipment/", name = "EquipmentBooking")
@XmlSeeAlso({ObjectFactory.class, ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface EquipmentBooking {

    @WebMethod(action = "http://www.bp.org/equipment/bookEquipment")
    @WebResult(name = "bookEquipmentResponse", targetNamespace = "http://www.bp.org", partName = "payload")
    public BookingInfo bookEquipment(

        @WebParam(partName = "payload", name = "bookEquipmentRequest", targetNamespace = "http://www.bp.org")
        BookEquipmentRequest payload
    ) throws BookingErrorMsg;

    @WebMethod(action = "http://www.bp.org/equipment/cancelBooking")
    @WebResult(name = "cancelBookingResponse", targetNamespace = "http://www.bp.org", partName = "payload")
    public BookingInfo cancelBooking(

        @WebParam(partName = "payload", name = "cancelBookingRequest", targetNamespace = "http://www.bp.org")
        CancelBookingRequest payload
    ) throws BookingErrorMsg;
}
