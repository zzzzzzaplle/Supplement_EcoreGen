import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class School {
    private String name;
    private List<Course> courses = new ArrayList<>();
    private List<Sector> sectors = new ArrayList<>();

    public School() {}
    public School(String name) { this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    public List<Sector> getSectors() { return sectors; }
    public void setSectors(List<Sector> sectors) { this.sectors = sectors; }

    public boolean addCourse(String id) {
        if (courses.stream().anyMatch(c -> c.getId().equals(id))) return false;
        return courses.add(new Course(id));
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = courses.stream().filter(c -> c.getId().equals(courseId)).findFirst().orElse(null);
        Sector sector = sectors.stream().filter(s -> s.getId().equals(sectorId)).findFirst().orElse(null);
        if (course == null || sector == null) return false;
        if (course.getSector() != null) course.getSector().removeCourse(course);
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        Sector s = new Sector(id);
        sectors.add(s);
        return s;
    }
}

class Course {
    private String id;
    private List<Document> documents = new ArrayList<>();
    private Sector sector;
    private List<Session> sessions = new ArrayList<>();

    public Course() {}
    public Course(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> docs) { this.documents = docs; }
    public Sector getSector() { return sector; }
    public void setSector(Sector s) { this.sector = s; }
    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public List<Session> addSession(LocalDate date) {
        if (sessions.stream().noneMatch(s -> s.getDate().equals(date))) {
            sessions.add(new Session(date));
        }
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        return sessions.removeIf(s -> s.getDate().equals(date) && s.getParticipants().isEmpty());
    }

    public boolean addDocument(Document doc) { return documents.add(doc); }
    public boolean removeDocument(Document doc) { return documents.remove(doc); }
}

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants = new ArrayList<>();
    private Trainer trainer;

    public Session() {}
    public Session(LocalDate date) { this.date = date; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer t) { this.trainer = t; }
    public List<Participant> getParticipants() { return registeredParticipants; }
    public void setParticipants(List<Participant> p) { this.registeredParticipants = p; }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (today.isBefore(date)) { this.trainer = t; return true; }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (today.isBefore(date)) { this.trainer = newTrainer; return true; }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (today.isBefore(date) && !registeredParticipants.contains(p)) {
            return registeredParticipants.add(p);
        }
        return false;
    }
}

class Sector {
    private String id;
    private List<Course> courses = new ArrayList<>();

    public Sector() {}
    public Sector(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    void addCourse(Course course) { courses.add(course); }
    void removeCourse(Course course) { courses.remove(course); }
}

class Document {
    private String name;

    public Document() {}
    public Document(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof Document)) return false; return Objects.equals(name, ((Document) o).name); }
    public int hashCode() { return Objects.hash(name); }
}

abstract class Person {
    private String id;
    public Person() {}
    public Person(String id) { this.id = id; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

class Participant extends Person {
    public Participant() {}
    public Participant(String id) { super(id); }
}

class Trainer extends Person {
    private boolean contractor;
    public Trainer() {}
    public Trainer(String id, boolean contractor) { super(id); this.contractor = contractor; }
    public boolean isContractor() { return contractor; }
    public void setContractor(boolean contractor) { this.contractor = contractor; }
}