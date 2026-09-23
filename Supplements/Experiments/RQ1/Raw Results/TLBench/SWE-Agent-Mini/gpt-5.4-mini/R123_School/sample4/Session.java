import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.registeredParticipants = new ArrayList<Participant>();
    }

    public Session(LocalDate date) {
        this();
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

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public void setParticipants(List<Participant> registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (date == null || t == null || today == null) return false;
        if (!today.isBefore(date)) return false;
        trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        return assignTrainer(newTrainer, today);
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (date == null || p == null || today == null) return false;
        if (today.isAfter(date)) return false;
        if (!registeredParticipants.contains(p)) {
            registeredParticipants.add(p);
        }
        return true;
    }
}
