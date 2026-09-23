import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = "";
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public School(String name) {
        this.name = name;
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    public List<Sector> getSectors() { return sectors; }
    public void setSectors(List<Sector> sectors) { this.sectors = sectors; }

    public boolean addCourse(String id) {
        if (id == null) return false;
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(id)) {
                return false;
            }
        }
        courses.add(new Course(id));
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        if (courseId == null || sectorId == null) return false;
        Course course = null;
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }
        if (course == null) return false;

        Sector sector = null;
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(sectorId)) {
                sector = s;
                break;
            }
        }
        if (sector == null) return false;

        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }

        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null) return null;
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(id)) {
                return s;
            }
        }
        Sector s = new Sector(id);
        sectors.add(s);
        return s;
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

    public Course(String id) {
        this.id = id;
        this.documents = new ArrayList<>();
        this.sector = null;
        this.sessions = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public List<Session> addSession(LocalDate date) {
        if (date == null) return sessions;
        for (Session s : sessions) {
            if (s.getDate() != null && s.getDate().equals(date)) {
                return sessions;
            }
        }
        sessions.add(new Session(date));
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        if (date == null) return false;
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            if (s.getDate() != null && s.getDate().equals(date)) {
                if (s.getParticipants() == null || s.getParticipants().isEmpty()) {
                    sessions.remove(i);
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
        documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        if (doc == null) return false;
        return documents.remove(doc);
    }
}

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    public Session() {
        this.date = null;
        this.registeredParticipants = new ArrayList<>();
        this.trainer = null;
    }

    public Session(LocalDate date) {
        this.date = date;
        this.registeredParticipants = new ArrayList<>();
        this.trainer = null;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }
    public List<Participant> getParticipants() { return registeredParticipants; }
    public void setParticipants(List<Participant> participants) { this.registeredParticipants = participants; }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null || this.date == null) return false;
        if (!today.isBefore(this.date)) return false;
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || this.date == null) return false;
        if (!today.isBefore(this.date)) return false;
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null) return false;
        if (registeredParticipants.contains(p)) return false;
        registeredParticipants.add(p);
        return true;
    }
}

class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.id = "";
        this.courses = new ArrayList<>();
    }

    public Sector(String id) {
        this.id = id;
        this.courses = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    void addCourse(Course course) {
        if (course == null) return;
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (course == null) return;
        courses.remove(course);
    }
}

class Document {
    private String name;

    public Document() {
        this.name = "";
    }

    public Document(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document d = (Document) o;
        return Objects.equals(name, d.name);
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

    public Person(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

class Participant extends Person {
    public Participant() {
        super();
    }

    public Participant(String id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant p = (Participant) o;
        return Objects.equals(getId(), p.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
        super();
        this.contractor = false;
    }

    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    public boolean isContractor() { return contractor; }
    public void setContractor(boolean contractor) { this.contractor = contractor; }
}