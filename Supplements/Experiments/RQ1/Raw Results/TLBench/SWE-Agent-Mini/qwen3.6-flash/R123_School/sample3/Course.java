import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
        this.id = null;
        this.documents = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.sector = null;
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
        Session toCancel = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                toCancel = s;
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
        for (Document d : documents) {
            if (d.equals(doc)) {
                return false;
            }
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
