import java.util.Objects;

public class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
    }

    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return contractor == trainer.contractor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contractor);
    }
}
