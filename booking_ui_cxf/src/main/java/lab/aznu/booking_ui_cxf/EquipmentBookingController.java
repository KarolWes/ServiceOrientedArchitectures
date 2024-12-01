package lab.aznu.booking_ui_cxf;

import java.net.URL;

import javax.xml.namespace.QName;

import bp.BookEquipmentRequest;
import bp.equipment.BookingErrorMsg;
import bp.equipment.EquipmentBooking;
import bp.equipment.EquipmentBookingEndpointService;
import bp.types.BookingInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EquipmentBookingController {

    @GetMapping("/bookEquipment")
    public String bookEquipmentForm(Model model) {
        BookEquipmentRequest brf = new BookEquipmentRequest();

        model.addAttribute("bookEquipmentRequest", brf);
        return "bookEquipment";
    }

    private static final QName SERVICE_NAME = new QName("http://equipment.bp.org/", "EquipmentBookingEndpointService");


    @PostMapping("/bookEquipment")
    public String bookEquipment(@ModelAttribute BookEquipmentRequest brf, Model model) {
        URL wsdlURL = EquipmentBookingEndpointService.WSDL_LOCATION;


        EquipmentBookingEndpointService ss = new EquipmentBookingEndpointService(wsdlURL, SERVICE_NAME);
        EquipmentBooking port = ss.getEquipmentBookingEndpointPort();

        try {
            BookingInfo _bookEquipment__return = port.bookEquipment(brf);
            System.out.println("bookEquipment.result=" + _bookEquipment__return);
            model.addAttribute("bookingInfo", _bookEquipment__return);
            return "result";


        } catch (BookingErrorMsg e) {
            System.out.println("Expected exception: BookingErrorMsg has occurred.");
            System.out.println(e);
            model.addAttribute("bookingErrorMsg", e);
            return "error";
        }
    }

}
