import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants = new ArrayList<Participant>();
    private Trainer trainer;

    public Session() {
    }

    public Session(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (date == null || today == null || !today.isBefore(date)) {
            return false;
        }
        trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (date == null || today == null || !today.isBefore(date)) {
            return false;
        }
        trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null) {
            return false;
        }
        for (Participant existing : registeredParticipants) {
            if (existing != null && existing.getId() != null && existing.getId().equals(p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
