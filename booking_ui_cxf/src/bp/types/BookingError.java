package bp.types;

import lombok.Data;

@Data
public class BookingError {
    private String message;
    private int code;
}
