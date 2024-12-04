package bp.equipment;


import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServiceClient(name = "EquipmentBookingEndpointService",
                  wsdlLocation = "file:/booking_ui_cxf/src/main/resources/equipment.wsdl",
                  targetNamespace = "http://equipment.bp.org/")
public class EquipmentBookingEndpointService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://equipment.bp.org/", "EquipmentBookingEndpointService");
    public final static QName EquipmentBookingEndpointPort = new QName("http://equipment.bp.org/", "EquipmentBookingEndpointPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/booking_ui_cxf/src/main/resources/equipment.wsdl");
        } catch (MalformedURLException e) {
            Logger.getLogger(EquipmentBookingEndpointService.class.getName())
                .log(Level.INFO,
                     "Can not initialize the default wsdl from {0}", "file:/booking_ui_cxf/src/main/resources/equipment.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public EquipmentBookingEndpointService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public EquipmentBookingEndpointService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EquipmentBookingEndpointService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public EquipmentBookingEndpointService(WebServiceFeature... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public EquipmentBookingEndpointService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public EquipmentBookingEndpointService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "EquipmentBookingEndpointPort")
    public EquipmentBooking getEquipmentBookingEndpointPort() {
        return super.getPort(EquipmentBookingEndpointPort, EquipmentBooking.class);
    }

    @WebEndpoint(name = "EquipmentBookingEndpointPort")
    public EquipmentBooking getEquipmentBookingEndpointPort(WebServiceFeature... features) {
        return super.getPort(EquipmentBookingEndpointPort, EquipmentBooking.class, features);
    }

}
