import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;

class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = "";
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public boolean addCourse(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        for (Course c : courses) {
            if (c.getId().equals(id)) {
                return false;
            }
        }
        Course newCourse = new Course(id);
        courses.add(newCourse);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        if (courseId == null || sectorId == null) {
            return false;
        }
        
        Course course = null;
        for (Course c : courses) {
            if (c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }
        
        if (course == null) {
            return false;
        }

        Sector sector = null;
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                sector = s;
                break;
            }
        }

        if (sector == null) {
            return false;
        }

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }
        
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null;
            }
        }
        Sector newSector = new Sector(id);
        sectors.add(newSector);
        return newSector;
    }
}

class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    public Course() {
        this.id = "";
        this.documents = new ArrayList<>();
        this.sector = null;
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
        if (date == null) {
            return sessions;
        }
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
        if (date == null) {
            return false;
        }
        Session sessionToRemove = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                sessionToRemove = s;
                break;
            }
        }
        
        if (sessionToRemove == null) {
            return false;
        }
        
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
        if (documents.contains(doc)) {
            return false;
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

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.date = LocalDate.now();
        this.registeredParticipants = new ArrayList<>();
        this.trainer = null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null) {
            return false;
        }
        if (trainer != null) {
            return false;
        }
        if (!today.isBefore(date)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null) {
            return false;
        }
        if (trainer == null) {
            return false;
        }
        if (!today.isBefore(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null) {
            return false;
        }
        if (!today.isBefore(date)) {
            return false;
        }
        if (registeredParticipants.contains(p)) {
            return false;
        }
        return registeredParticipants.add(p);
    }
}

class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.id = "";
        this.courses = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Course> getCourses() {
        return courses;
    }

    void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (course != null) {
            courses.remove(course);
        }
    }
}

class Document {
    private String name;

    public Document() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

abstract class Person {
    private String id;

    public Person() {
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class Participant extends Person {

    public Participant() {
        super();
    }
}

class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
        super();
        this.contractor = false;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}