import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a school that manages courses and sectors.
 */
class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    /**
     * Unparameterized constructor.
     */
    public School() {
        this.name = null;
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    /**
     * Constructs a school with the specified name.
     *
     * @param name the unique school name
     */
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

    /**
     * Adds a new course with the given unique ID.
     *
     * @param id the course ID
     * @return true if added successfully; otherwise false
     */
    public boolean addCourse(String id) {
        if (id == null || findCourseById(id) != null) {
            return false;
        }
        courses.add(new Course(id));
        return true;
    }

    /**
     * Assigns a course to a sector. A course can belong to exactly one sector at a time.
     *
     * @param courseId the course ID
     * @param sectorId the sector ID
     * @return true if successful; otherwise false
     */
    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourseById(courseId);
        Sector newSector = findSectorById(sectorId);
        if (course == null || newSector == null) {
            return false;
        }

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        course.setSector(newSector);
        newSector.addCourse(course);
        return true;
    }

    /**
     * Adds a new sector if it does not already exist.
     *
     * @param id the sector ID
     * @return the existing or newly created sector, or null if id is null
     */
    public Sector addSector(String id) {
        if (id == null) {
            return null;
        }
        Sector existing = findSectorById(id);
        if (existing != null) {
            return existing;
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }

    private Course findCourseById(String id) {
        for (Course course : courses) {
            if (Objects.equals(course.getId(), id)) {
                return course;
            }
        }
        return null;
    }

    private Sector findSectorById(String id) {
        for (Sector sector : sectors) {
            if (Objects.equals(sector.getId(), id)) {
                return sector;
            }
        }
        return null;
    }
}

/**
 * Represents a course with documents, sessions, and an optional sector.
 */
class Course {
    private String id;
    private List<Document> documents;
    private Sector sector;
    private List<Session> sessions;

    /**
     * Unparameterized constructor.
     */
    public Course() {
        this.id = null;
        this.documents = new ArrayList<>();
        this.sector = null;
        this.sessions = new ArrayList<>();
    }

    /**
     * Constructs a course with the specified ID.
     *
     * @param id the course ID
     */
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
        this.documents = (docs == null) ? new ArrayList<>() : docs;
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

    /**
     * Adds a session on the specified date if no session exists for that day.
     *
     * @param date the session date
     * @return the updated list of sessions
     */
    public List<Session> addSession(LocalDate date) {
        if (date == null) {
            return sessions;
        }
        for (Session session : sessions) {
            if (date.equals(session.getDate())) {
                return sessions;
            }
        }
        sessions.add(new Session(date));
        return sessions;
    }

    /**
     * Cancels a session on the given date only if it has no participants.
     *
     * @param date the session date
     * @return true if removed successfully; otherwise false
     */
    public boolean cancelSession(LocalDate date) {
        if (date == null) {
            return false;
        }
        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            if (date.equals(session.getDate())) {
                if (session.getParticipants().isEmpty()) {
                    sessions.remove(i);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Adds a document to this course if not already present.
     *
     * @param doc the document
     * @return true on success; otherwise false
     */
    public boolean addDocument(Document doc) {
        if (doc == null || documents.contains(doc)) {
            return false;
        }
        documents.add(doc);
        return true;
    }

    /**
     * Removes a document from this course.
     *
     * @param doc the document
     * @return true on success; otherwise false
     */
    public boolean removeDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        return documents.remove(doc);
    }
}

/**
 * Represents a session for a course.
 */
class Session {
    private LocalDate date;
    private List<Participant> registeredParticipants;
    private Trainer trainer;

    /**
     * Unparameterized constructor.
     */
    public Session() {
        this.date = null;
        this.registeredParticipants = new ArrayList<>();
        this.trainer = null;
    }

    /**
     * Constructs a session for the specified date.
     *
     * @param date the session date
     */
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

    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    /**
     * Assigns a trainer to the session before its start date.
     *
     * @param t the trainer
     * @param today the current date
     * @return true if successful; otherwise false
     */
    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null || date == null || !today.isBefore(date)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    /**
     * Replaces the trainer before the session's start date.
     *
     * @param newTrainer the new trainer
     * @param today the current date
     * @return true if successful; otherwise false
     */
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || date == null || !today.isBefore(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    /**
     * Registers a participant before the session date and without duplicate enrollment.
     *
     * @param p the participant
     * @param today the current date
     * @return true if successful; otherwise false
     */
    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null || date == null || !today.isBefore(date)) {
            return false;
        }
        for (Participant participant : registeredParticipants) {
            if (Objects.equals(participant.getId(), p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}

/**
 * Represents a sector containing courses.
 */
class Sector {
    private String id;
    private List<Course> courses;

    /**
     * Unparameterized constructor.
     */
    public Sector() {
        this.id = null;
        this.courses = new ArrayList<>();
    }

    /**
     * Constructs a sector with the specified ID.
     *
     * @param id the sector ID
     */
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

    void addCourse(Course course) {
        if (course == null || courses.contains(course)) {
            return;
        }
        courses.add(course);
    }

    void removeCourse(Course course) {
        if (course == null) {
            return;
        }
        courses.remove(course);
        if (course.getSector() == this) {
            course.setSector(null);
        }
    }
}

/**
 * Represents a document referenced by a course.
 */
class Document {
    private String name;

    /**
     * Unparameterized constructor.
     */
    public Document() {
        this.name = null;
    }

    /**
     * Constructs a document with the specified name.
     *
     * @param name the document name
     */
    public Document(String name) {
        this();
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

/**
 * Abstract base class for people in the school.
 */
abstract class Person {
    private String id;

    /**
     * Unparameterized constructor.
     */
    public Person() {
        this.id = null;
    }

    /**
     * Constructs a person with the specified ID.
     *
     * @param id the unique person ID
     */
    public Person(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

/**
 * Represents a participant who can register for sessions.
 */
class Participant extends Person {
    /**
     * Unparameterized constructor.
     */
    public Participant() {
        super();
    }

    /**
     * Constructs a participant with the specified ID.
     *
     * @param id the participant ID
     */
    public Participant(String id) {
        super(id);
    }
}

/**
 * Represents a trainer who may be a contractor.
 */
class Trainer extends Person {
    private boolean contractor;

    /**
     * Unparameterized constructor.
     */
    public Trainer() {
        super();
        this.contractor = false;
    }

    /**
     * Constructs a trainer with the specified ID and contractor flag.
     *
     * @param id the trainer ID
     * @param contractor whether the trainer is an individual contractor
     */
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