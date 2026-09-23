import java.util.List;
import java.time.LocalDate;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this(null);
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new java.util.ArrayList<>();
        this.trainer = null;
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
        if (t == null) {
            return false;
        }
        // Check if the session's start date is in the future
        if (date.isBefore(today)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null) {
            return false;
        }
        // Check if the session's start date is in the future
        if (date.isBefore(today)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null) {
            return false;
        }
        // Check for duplicate enrollment
        for (Participant existing : registeredParticipants) {
            if (existing.getId() != null && existing.getId().equals(p.getId())) {
                return false;
            }
        }
        return registeredParticipants.add(p);
    }
}
