import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
        this("");
    }

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
        // Check if a session already exists for this date
        for (Session session : sessions) {
            if (session.getDate().equals(date)) {
                return sessions;
            }
        }
        Session session = new Session(date);
        sessions.add(session);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session sessionToRemove = null;
        for (Session session : sessions) {
            if (session.getDate().equals(date)) {
                sessionToRemove = session;
                break;
            }
        }
        if (sessionToRemove == null) {
            return false;
        }
        // Only cancel if no participants
        if (!sessionToRemove.getParticipants().isEmpty()) {
            return false;
        }
        sessions.remove(sessionToRemove);
        return true;
    }

    public boolean addDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        for (Document existing : documents) {
            if (existing.equals(doc)) {
                return false;
            }
        }
        return documents.add(doc);
    }

    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}
