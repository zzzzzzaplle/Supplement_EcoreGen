import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        this();
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
            if (session.getDate().equals(date)) {
                return sessions;
            }
        }
        Session newSession = new Session(date);
        sessions.add(newSession);
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        Optional<Session> toCancel = sessions.stream()
                .filter(s -> s.getDate().equals(date))
                .findFirst();
        if (toCancel.isPresent()) {
            Session session = toCancel.get();
            if (session.getParticipants().isEmpty()) {
                sessions.remove(session);
                return true;
            }
        }
        return false;
    }

    public boolean addDocument(Document doc) {
        if (doc != null && !documents.contains(doc)) {
            documents.add(doc);
            return true;
        }
        return false;
    }

    public boolean removeDocument(Document doc) {
        if (doc != null && documents.contains(doc)) {
            documents.remove(doc);
            return true;
        }
        return false;
    }
}
