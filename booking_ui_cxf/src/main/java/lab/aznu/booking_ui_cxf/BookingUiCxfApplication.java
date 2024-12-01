package lab.aznu.booking_ui_cxf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingUiCxfApplication {

    public static void main(String[] args) {
        System.setProperty("jakarta.xml.ws.spi.Provider", "com.sun.xml.ws.spi.ProviderImpl");
        SpringApplication.run(BookingUiCxfApplication.class, args);
    }

}
