import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        for (Session session : sessions) {
            if (session.getDate().isEqual(date)) {
                return sessions;
            }
        }
        Session newSession = new Session(date);
        sessions.add(newSession);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session toCancel = null;
        for (Session session : sessions) {
            if (session.getDate().isEqual(date)) {
                toCancel = session;
                break;
            }
        }
        if (toCancel == null) {
            return false;
        }
        if (!toCancel.getParticipants().isEmpty()) {
            return false;
        }
        sessions.remove(toCancel);
        return true;
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
        return documents.remove(doc);
    }

    public Session findSession(LocalDate date) {
        for (Session session : sessions) {
            if (session.getDate().isEqual(date)) {
                return session;
            }
        }
        return null;
    }
}
