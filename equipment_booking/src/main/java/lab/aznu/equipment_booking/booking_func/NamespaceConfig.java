package lab.aznu.equipment_booking.booking_func;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "soap")
public class NamespaceConfig {
    private String uri = "http://example.com/booking";
}

