import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private List<Document> documents = new ArrayList<Document>();
    private Sector sector;
    private List<Session> sessions = new ArrayList<Session>();

    public Course() {
    }

    public Course(String id) {
        this.id = id;
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
        documents = docs;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector s) {
        sector = s;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Session> addSession(LocalDate date) {
        for (Session session : sessions) {
            if (session != null && date != null && date.equals(session.getDate())) {
                return sessions;
            }
        }
        sessions.add(new Session(date));
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            if (session != null && date != null && date.equals(session.getDate())) {
                if (session.getParticipants() != null && !session.getParticipants().isEmpty()) {
                    return false;
                }
                sessions.remove(i);
                return true;
            }
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
        return documents.remove(doc);
    }
}
