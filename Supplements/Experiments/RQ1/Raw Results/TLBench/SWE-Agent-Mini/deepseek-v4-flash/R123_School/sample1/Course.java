import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;
    
    public Course(String id) {
        this.id = id;
        this.documents = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }
    
    public Course() {
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
    
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
    
    public List<Session> addSession(LocalDate date) {
        if (date == null) {
            return sessions;
        }
        boolean exists = false;
        for (Session s : sessions) {
            if (s.getDate() != null && s.getDate().equals(date)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            Session newSession = new Session(date);
            sessions.add(newSession);
        }
        return sessions;
    }
    
    public boolean cancelSession(LocalDate date) {
        if (date == null) {
            return false;
        }
        Session toRemove = null;
        for (Session s : sessions) {
            if (s.getDate() != null && s.getDate().equals(date)) {
                if (s.getParticipants() == null || s.getParticipants().isEmpty()) {
                    toRemove = s;
                } else {
                    return false;
                }
                break;
            }
        }
        if (toRemove != null) {
            sessions.remove(toRemove);
            return true;
        }
        return false;
    }
    
    public boolean addDocument(Document doc) {
        if (doc == null || documents.contains(doc)) {
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
