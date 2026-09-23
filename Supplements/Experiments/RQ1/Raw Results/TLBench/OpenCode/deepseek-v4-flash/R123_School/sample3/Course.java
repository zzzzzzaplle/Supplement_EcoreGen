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
        boolean exists = sessions.stream().anyMatch(s -> s.getDate().equals(date));
        if (!exists) {
            sessions.add(new Session(date));
        }
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Session target = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                target = s;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!target.getParticipants().isEmpty()) {
            return false;
        }
        sessions.remove(target);
        return true;
    }

    public boolean addDocument(Document doc) {
        if (!documents.contains(doc)) {
            documents.add(doc);
            return true;
        }
        return false;
    }

    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}
