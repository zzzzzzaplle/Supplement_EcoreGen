import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

class Participant extends Person {

    public Participant() {
    }

    public Participant(String id) {
        super(id);
    }
}

class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
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
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

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

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    public void setParticipants(List<Participant> participants) {
        this.registeredParticipants = participants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (today.isBefore(this.date)) {
            this.trainer = t;
            return true;
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (today.isBefore(this.date)) {
            this.trainer = newTrainer;
            return true;
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (this.registeredParticipants.contains(p)) {
            return false;
        }
        this.registeredParticipants.add(p);
        return true;
    }
}

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

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    void addCourse(Course course) {
        if (!this.courses.contains(course)) {
            this.courses.add(course);
        }
    }

    void removeCourse(Course course) {
        this.courses.remove(course);
    }
}

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

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Session> addSession(LocalDate date) {
        for (Session s : this.sessions) {
            if (s.getDate().equals(date)) {
                return this.sessions;
            }
        }
        this.sessions.add(new Session(date));
        return this.sessions;
    }

    public boolean cancelSession(LocalDate date) {
        for (Session s : this.sessions) {
            if (s.getDate().equals(date)) {
                if (s.getParticipants() == null || s.getParticipants().isEmpty()) {
                    this.sessions.remove(s);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean addDocument(Document doc) {
        if (this.documents.contains(doc)) {
            return false;
        }
        this.documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        if (this.documents.contains(doc)) {
            this.documents.remove(doc);
            return true;
        }
        return false;
    }
}

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

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    public boolean addCourse(String id) {
        for (Course c : this.courses) {
            if (c.getId() != null && c.getId().equals(id)) {
                return false;
            }
        }
        this.courses.add(new Course(id));
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course targetCourse = null;
        for (Course c : this.courses) {
            if (c.getId() != null && c.getId().equals(courseId)) {
                targetCourse = c;
                break;
            }
        }
        if (targetCourse == null) {
            return false;
        }
        Sector targetSector = null;
        for (Sector s : this.sectors) {
            if (s.getId() != null && s.getId().equals(sectorId)) {
                targetSector = s;
                break;
            }
        }
        if (targetSector == null) {
            return false;
        }
        Sector currentSector = targetCourse.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(targetCourse);
        }
        targetCourse.setSector(targetSector);
        targetSector.addCourse(targetCourse);
        return true;
    }

    public Sector addSector(String id) {
        for (Sector s : this.sectors) {
            if (s.getId() != null && s.getId().equals(id)) {
                return s;
            }
        }
        Sector newSector = new Sector(id);
        this.sectors.add(newSector);
        return newSector;
    }
}