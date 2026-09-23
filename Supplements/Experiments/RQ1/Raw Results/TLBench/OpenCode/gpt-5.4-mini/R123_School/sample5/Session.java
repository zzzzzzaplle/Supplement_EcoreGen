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
        if (t == null || today == null || date == null) {
            return false;
        }
        if (today.isBefore(date) || today.equals(date)) {
            this.trainer = t;
            return true;
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || date == null) {
            return false;
        }
        if (today.isBefore(date) || today.equals(date)) {
            this.trainer = newTrainer;
            return true;
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null || date == null) {
            return false;
        }
        if (today.isAfter(date)) {
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
