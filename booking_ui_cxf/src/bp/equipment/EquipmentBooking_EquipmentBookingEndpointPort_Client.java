
package bp.equipment;


import bp.BookEquipmentRequest;
import bp.CancelBookingRequest;
import bp.types.BookingError;
import bp.types.BookingInfo;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class EquipmentBooking_EquipmentBookingEndpointPort_Client {

    private static final QName SERVICE_NAME = new QName("http://equipment.bp.org/", "EquipmentBookingEndpointService");

    private EquipmentBooking_EquipmentBookingEndpointPort_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = EquipmentBookingEndpointService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        EquipmentBookingEndpointService ss = new EquipmentBookingEndpointService(wsdlURL, SERVICE_NAME);
        EquipmentBooking port = ss.getEquipmentBookingEndpointPort();

        {
        System.out.println("Invoking bookEquipment...");
        BookEquipmentRequest _bookEquipment_payload = null;
        try {
            BookingInfo _bookEquipment__return = port.bookEquipment(_bookEquipment_payload);
            System.out.println("bookEquipment.result=" + _bookEquipment__return);

        } catch (BookingErrorMsg e) {
            System.out.println("Expected exception: BookingErrorMsg has occurred.");
            System.out.println(e.toString());
        }

        }
        {
        System.out.println("Invoking cancelBooking...");
        CancelBookingRequest _cancelBooking_payload = null;
        try {
            BookingInfo _cancelBooking__return = port.cancelBooking(_cancelBooking_payload);
            System.out.println("cancelBooking.result=" + _cancelBooking__return);

        } catch (BookingErrorMsg e) {
            System.out.println("Expected exception: BookingErrorMsg has occurred.");
            System.out.println(e.toString());
        }

            }

        System.exit(0);
    }

}
