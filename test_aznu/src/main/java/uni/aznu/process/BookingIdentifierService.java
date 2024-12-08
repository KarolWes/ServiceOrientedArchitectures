package uni.aznu.process;


import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class BookingIdentifierService {

    public String getBookingIdentifier() {
        return UUID.randomUUID().toString();
    }


}
