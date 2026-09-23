import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.date = null;
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

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || date == null || today == null) {
            return false;
        }
        if (!date.isAfter(today)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || date == null || today == null) {
            return false;
        }
        if (!date.isAfter(today)) {
            return false;
        }
        if (this.trainer == null) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || date == null || today == null) {
            return false;
        }
        if (!date.isAfter(today)) {
            return false;
        }
        if (registeredParticipants.contains(p)) {
            return false;
        }
        registeredParticipants.add(p);
        return true;
    }
}
