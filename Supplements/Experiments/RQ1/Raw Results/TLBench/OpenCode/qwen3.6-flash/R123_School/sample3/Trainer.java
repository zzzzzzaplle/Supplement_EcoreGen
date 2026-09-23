
import java.time.LocalDate;
import java.util.*;

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

    public boolean assignTrainer(Trainer t, LocalDate today, LocalDate sessionDate) {
        if (today.isAfter(sessionDate)) {
            return false;
        }
        return true;
    }
}
