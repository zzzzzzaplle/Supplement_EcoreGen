import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// ---------- Document class ----------
class Document {
    private String name;

    public Document() {
    }

    public Document(String name) {
        this.name = name;
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
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

// ---------- Person (abstract) ----------
abstract class Person {
    private String id;

    public Person() {
    }

    public Person(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

// ---------- Participant ----------
class Participant extends Person {
    public Participant() {
        super();
    }

    public Participant(String id) {
        super(id);
    }
}

// ---------- Trainer ----------
class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
        super();
    }

    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}

// ---------- Sector ----------
class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.courses = new ArrayList<>();
    }

    public Sector(String id) {
        this.id = id;
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
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        courses.remove(course);
    }
}

// ---------- Session ----------
class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.registeredParticipants = new ArrayList<>();
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<>();
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
        if (date.isBefore(today)) {
            return false;
        }
        if (this.trainer != null) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (date.isBefore(today)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (registeredParticipants.contains(p)) {
            return false;
        }
        registeredParticipants.add(p);
        return true;
    }
}

// ---------- Course ----------
class Course {
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
        // ensure only one session per day
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
        if (target == null) return false;
        // only sessions without participants may be removed
        if (target.getParticipants().isEmpty()) {
            sessions.remove(target);
            return true;
        }
        return false;
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

// ---------- School ----------
class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public School(String name) {
        this.name = name;
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
        // case-sensitive unique ID
        boolean exists = courses.stream().anyMatch(c -> c.getId().equals(id));
        if (exists) return false;
        courses.add(new Course(id));
        return true;
    }

    public Sector addSector(String id) {
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = null;
        for (Course c : courses) {
            if (c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }
        Sector sector = null;
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                sector = s;
                break;
            }
        }
        if (course == null || sector == null) return false;

        // remove from current sector if any
        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }
        // add to new sector
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }
}