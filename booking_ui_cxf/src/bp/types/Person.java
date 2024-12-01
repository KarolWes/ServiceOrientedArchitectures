package bp.types;

import lombok.Data;

@Data
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean student;

    public Person(String id){
        this.id = id;
    }
    public Person(){}
}
