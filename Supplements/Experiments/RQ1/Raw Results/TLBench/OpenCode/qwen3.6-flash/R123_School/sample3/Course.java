
import java.time.LocalDate;
import java.util.*;

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
        for (Session s : sessions) {
            if (s.getDate().isEqual(date)) {
                return sessions;
            }
        }
        Session session = new Session(date);
        sessions.add(session);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session toRemove = null;
        for (Session s : sessions) {
            if (s.getDate().isEqual(date)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove == null) {
            return false;
        }
        if (toRemove.getParticipants().size() > 0) {
            return false;
        }
        return sessions.remove(toRemove);
    }

    public boolean addDocument(Document doc) {
        return documents.add(doc);
    }

    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}

