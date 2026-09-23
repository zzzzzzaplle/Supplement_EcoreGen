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
        if (date == null || today == null || !today.isBefore(date) || p == null) {
            return false;
        }
        for (Participant participant : registeredParticipants) {
            if (p.getId() != null && p.getId().equals(participant.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
