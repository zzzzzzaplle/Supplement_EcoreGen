import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.date = null;
        this.registeredParticipants = new ArrayList<Participant>();
        this.trainer = null;
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<Participant>();
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

    public void setParticipants(List<Participant> participants) {
        this.registeredParticipants = participants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || date == null) {
            return false;
        }
        if (today != null && today.isBefore(date)) {
            this.trainer = t;
            return true;
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || date == null) {
            return false;
        }
        if (today != null && today.isBefore(date)) {
            this.trainer = newTrainer;
            return true;
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null) {
            return false;
        }
        if (registeredParticipants == null) {
            registeredParticipants = new ArrayList<Participant>();
        }
        for (Participant existing : registeredParticipants) {
            if (existing.getId() != null && existing.getId().equals(p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
