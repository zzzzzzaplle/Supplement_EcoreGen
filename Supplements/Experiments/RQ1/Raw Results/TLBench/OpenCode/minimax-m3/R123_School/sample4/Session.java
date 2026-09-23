import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.date = LocalDate.now();
        this.registeredParticipants = new ArrayList<>();
        this.trainer = null;
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<>();
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

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public void setRegisteredParticipants(List<Participant> registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null || date == null) {
            return false;
        }
        if (this.trainer != null) {
            return false;
        }
        if (!today.isBefore(date)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || date == null) {
            return false;
        }
        if (this.trainer == null) {
            return false;
        }
        if (!today.isBefore(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null) {
            return false;
        }
        if (registeredParticipants.contains(p)) {
            return false;
        }
        registeredParticipants.add(p);
        return true;
    }
}
