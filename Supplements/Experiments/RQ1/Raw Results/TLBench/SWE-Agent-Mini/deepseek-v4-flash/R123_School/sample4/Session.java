import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.registeredParticipants = new ArrayList<>();
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<>();
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

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null) {
            return false;
        }
        // Before the session's start date
        if (today.isBefore(date)) {
            // If no trainer assigned yet
            if (trainer == null) {
                trainer = t;
                return true;
            }
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null) {
            return false;
        }
        // Before the session's start date
        if (today.isBefore(date)) {
            // Only replace if there is already a trainer
            if (trainer != null) {
                trainer = newTrainer;
                return true;
            }
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null) {
            return false;
        }
        // No duplicate enrollments for the same participant
        for (Participant existing : registeredParticipants) {
            if (existing.getId().equals(p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
