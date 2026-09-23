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
        if (date == null || today == null || !today.isBefore(date)) return false;
        trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        return assignTrainer(newTrainer, today);
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || date == null || today == null || today.isAfter(date)) return false;
        if (registeredParticipants == null) registeredParticipants = new ArrayList<>();
        for (Participant participant : registeredParticipants) {
            if (participant != null && participant.getId() != null && participant.getId().equals(p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}
