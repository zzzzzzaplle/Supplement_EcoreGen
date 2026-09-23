import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    // Unparameterized constructor
    public School() {
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public School(String name) {
        this();
        this.name = name;
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
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                return false; // duplicate id
            }
        }
        Course newCourse = new Course(id);
        courses.add(newCourse);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course courseToAssign = null;
        Sector newSector = null;

        // Find course
        for (Course c : courses) {
            if (c.getId().equals(courseId)) {
                courseToAssign = c;
                break;
            }
        }
        // Find sector
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                newSector = s;
                break;
            }
        }
        if (courseToAssign == null || newSector == null) {
            return false;
        }

        // Remove from current sector if any
        if (courseToAssign.getSector() != null) {
            courseToAssign.getSector().removeCourse(courseToAssign);
        }
        // Assign to new sector
        newSector.addCourse(courseToAssign);
        courseToAssign.setSector(newSector);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null; // duplicate id
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

    // Unparameterized constructor
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
        // Ensure only one session per course per day
        boolean exists = false;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
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
        Session toRemove = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                // Only sessions without participants may be removed
                if (s.getParticipants().isEmpty()) {
                    toRemove = s;
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
        if (documents.contains(doc)) {
            return false;
        }
        documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}

class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    // Unparameterized constructor
    public Session() {
        this.registeredParticipants = new ArrayList<>();
    }

    public Session(LocalDate date) {
        this();
        this.date = date;
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

    public boolean assignTrainer(Trainer t, LocalDate today) {
        // Before the session's start date
        if (today.isBefore(date) && !today.isEqual(date)) {
            if (trainer == null) {
                trainer = t;
                return true;
            }
        }
        return false;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        // Before the session's start date
        if (today.isBefore(date) && !today.isEqual(date)) {
            if (trainer != null) {
                trainer = newTrainer;
                return true;
            }
        }
        return false;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        // No duplicate enrollments for the same participant
        for (Participant participant : registeredParticipants) {
            if (participant.getId().equals(p.getId())) {
                return false;
            }
        }
        if (today.isBefore(date) && !today.isEqual(date)) {
            registeredParticipants.add(p);
            return true;
        }
        return false;
    }
}

class Sector {
    private String id;
    private List<Course> courses;

    // Unparameterized constructor
    public Sector() {
        this.courses = new ArrayList<>();
    }

    public Sector(String id) {
        this();
        this.id = id;
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

    // package-private (default) access modifier
    void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        courses.remove(course);
    }
}

class Document {
    private String name;

    // Unparameterized constructor
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

abstract class Person {
    private String id;

    // Unparameterized constructor
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
    // Unparameterized constructor
    public Participant() {
        super();
    }

    public Participant(String id) {
        super(id);
    }
}

class Trainer extends Person {
    private boolean contractor;

    // Unparameterized constructor
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