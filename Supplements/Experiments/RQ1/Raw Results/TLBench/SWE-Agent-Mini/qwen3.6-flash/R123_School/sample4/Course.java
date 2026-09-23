import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
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
        // Check if there's already a session on the same date
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                return sessions;
            }
        }
        Session session = new Session(date);
        sessions.add(session);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session session = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                session = s;
                break;
            }
        }
        if (session == null) {
            return false;
        }
        // Only sessions without participants can be removed
        if (!session.getParticipants().isEmpty()) {
            return false;
        }
        sessions.remove(session);
        return true;
    }

    public boolean addDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        for (Document d : documents) {
            if (d.equals(doc)) {
                return false;
            }
        }
        return documents.add(doc);
    }

    public boolean removeDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        return documents.remove(doc);
    }
}
