import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;
    
    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<>();
    }
    
    public Session() {
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
    
    public void setParticipants(List<Participant> participants) {
        this.registeredParticipants = participants;
    }
    
    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (date == null || t == null || today == null) {
            return false;
        }
        if (today.isBefore(date)) {
            this.trainer = t;
            return true;
        }
        return false;
    }
    
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (date == null || newTrainer == null || today == null) {
            return false;
        }
        if (today.isBefore(date)) {
            this.trainer = newTrainer;
            return true;
        }
        return false;
    }
    
    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null || date == null) {
            return false;
        }
        if (registeredParticipants.contains(p)) {
            return false;
        }
        registeredParticipants.add(p);
        return true;
    }
}
