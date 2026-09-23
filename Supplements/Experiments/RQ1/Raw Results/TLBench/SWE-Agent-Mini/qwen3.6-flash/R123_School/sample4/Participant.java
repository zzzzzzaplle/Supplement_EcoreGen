import java.util.Objects;

public class Participant extends Person {

    public Participant() {
    }

    public Participant(String id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Participant that = (Participant) o;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
