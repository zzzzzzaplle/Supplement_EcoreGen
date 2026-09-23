import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
        this.documents = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    public Course(String id) {
        this.id = id;
        this.documents = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Session> addSession(LocalDate date) {
        if (date == null) {
            return sessions;
        }
        for (Session s : sessions) {
            if (s.getDate() != null && s.getDate().equals(date)) {
                return sessions;
            }
        }
        sessions.add(new Session(date));
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        if (date == null) {
            return false;
        }
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            if (s.getDate() != null && s.getDate().equals(date)) {
                List<Participant> participants = s.getRegisteredParticipants();
                if (participants == null || participants.isEmpty()) {
                    sessions.remove(i);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean addDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        if (documents.contains(doc)) {
            return false;
        }
        documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        return documents.remove(doc);
    }
}
