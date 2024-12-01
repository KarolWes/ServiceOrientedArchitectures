package bp.types;

import lombok.Data;

@Data
public class Equipment {
    private int id;
    private String name;
    private String type = "Helmet";
    private String description;
    private BookingState state = BookingState.FREE;

    public Equipment(int id) {
        this.id = id;
    }
    public Equipment(){}
}
