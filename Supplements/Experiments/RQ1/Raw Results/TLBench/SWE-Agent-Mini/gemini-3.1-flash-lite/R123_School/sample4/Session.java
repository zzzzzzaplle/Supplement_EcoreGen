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

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public List<Participant> getParticipants() { return registeredParticipants; }
    public void setRegisteredParticipants(List<Participant> p) { this.registeredParticipants = p; }
    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer t) { this.trainer = t; }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (today.isBefore(date)) {
            this.trainer = t;
            return true;
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (today.isBefore(date)) {
            this.trainer = newTrainer;
            return true;
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (registeredParticipants.contains(p)) return false;
        registeredParticipants.add(p);
        return true;
    }
}
