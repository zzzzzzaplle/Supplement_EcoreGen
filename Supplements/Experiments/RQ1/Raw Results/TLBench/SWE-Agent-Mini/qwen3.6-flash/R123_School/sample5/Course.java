import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course(String id) {
        this.id = id;
        this.documents = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.sector = null;
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

    public void setDocuments(List<Document> docs) {
        this.documents = docs;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector s) {
        this.sector = s;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Session> addSession(LocalDate date) {
        // Check if session on same date already exists
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                throw new IllegalArgumentException("Session already exists on this date");
            }
        }
        Session session = new Session(date);
        sessions.add(session);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session sessionToRemove = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                sessionToRemove = s;
                break;
            }
        }
        if (sessionToRemove == null) {
            return false;
        }

        // Check if session has participants
        if (!sessionToRemove.getParticipants().isEmpty()) {
            return false;
        }

        sessions.remove(sessionToRemove);
        return true;
    }

    public boolean addDocument(Document doc) {
        return documents.add(doc);
    }

    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}
