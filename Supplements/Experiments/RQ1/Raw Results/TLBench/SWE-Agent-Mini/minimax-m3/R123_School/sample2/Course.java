import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
        this.id = "";
        this.documents = new ArrayList<Document>();
        this.sector = null;
        this.sessions = new ArrayList<Session>();
    }

    public Course(String id) {
        this.id = id;
        this.documents = new ArrayList<Document>();
        this.sector = null;
        this.sessions = new ArrayList<Session>();
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
        if (date == null) return sessions;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                return sessions;
            }
        }
        Session newSession = new Session(date);
        sessions.add(newSession);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        if (date == null) return false;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                if (s.getParticipants().isEmpty()) {
                    sessions.remove(s);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean addDocument(Document doc) {
        if (doc == null) return false;
        if (documents.contains(doc)) return false;
        documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        if (doc == null) return false;
        return documents.remove(doc);
    }
}
