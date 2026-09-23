import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Person {
    private String id;

    public Person() {}

    public Person(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
