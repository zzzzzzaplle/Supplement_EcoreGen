import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class Session {
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

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (today.isAfter(date) || today.isEqual(date)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (today.isAfter(date) || today.isEqual(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (today.isAfter(date) || today.isEqual(date)) {
            return false;
        }
        for (Participant existing : registeredParticipants) {
            if (existing.getId().equals(p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
