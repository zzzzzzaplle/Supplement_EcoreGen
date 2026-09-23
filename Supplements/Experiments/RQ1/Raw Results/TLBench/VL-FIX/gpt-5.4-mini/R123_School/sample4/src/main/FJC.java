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
     * Constructs a school with the given unique name.
     *
     * @param name school name
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
     * Adds a new course with the provided unique case-sensitive ID.
     *
     * @param id course id
     * @return true if added successfully; false otherwise
     */
    public boolean addCourse(String id) {
        if (id == null || id.isBlank() || findCourseById(id) != null) {
            return false;
        }
        courses.add(new Course(id));
        return true;
    }

    /**
     * Assigns a course to a sector, removing it from its current sector first if needed.
     *
     * @param courseId course id
     * @param sectorId sector id
     * @return true if successful; false otherwise
     */
    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourseById(courseId);
        Sector newSector = findSectorById(sectorId);
        if (course == null || newSector == null) {
            return false;
        }

        Sector current = course.getSector();
        if (current != null) {
            current.removeCourse(course);
            course.setSector(null);
        }

        newSector.addCourse(course);
        course.setSector(newSector);
        return true;
    }

    /**
     * Adds a new sector with the provided unique ID, or returns an existing sector if already present.
     *
     * @param id sector id
     * @return the created or existing sector; null if id is invalid
     */
    public Sector addSector(String id) {
        if (id == null || id.isBlank()) {
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
            if (course != null && Objects.equals(course.getId(), id)) {
                return course;
            }
        }
        return null;
    }

    private Sector findSectorById(String id) {
        for (Sector sector : sectors) {
            if (sector != null && Objects.equals(sector.getId(), id)) {
                return sector;
            }
        }
        return null;
    }
}

/**
 * Represents a course with sessions and documents.
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
     * Constructs a course with the given unique ID.
     *
     * @param id course id
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
     * Adds a session for the given date if no session exists for that day.
     *
     * @param date session date
     * @return up-to-date list of sessions
     */
    public List<Session> addSession(LocalDate date) {
        if (date == null) {
            return sessions;
        }
        if (findSessionByDate(date) == null) {
            sessions.add(new Session(date));
        }
        return sessions;
    }

    /**
     * Cancels a session for the given date only if it has no participants.
     *
     * @param date session date
     * @return true if removed successfully; false otherwise
     */
    public boolean cancelSession(LocalDate date) {
        Session session = findSessionByDate(date);
        if (session == null || !session.getParticipants().isEmpty()) {
            return false;
        }
        return sessions.remove(session);
    }

    /**
     * Adds a document to the course if not already present.
     *
     * @param doc document
     * @return true if added successfully; false otherwise
     */
    public boolean addDocument(Document doc) {
        if (doc == null || documents.contains(doc)) {
            return false;
        }
        documents.add(doc);
        return true;
    }

    /**
     * Removes a document from the course.
     *
     * @param doc document
     * @return true if removed successfully; false otherwise
     */
    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }

    private Session findSessionByDate(LocalDate date) {
        for (Session session : sessions) {
            if (session != null && Objects.equals(session.getDate(), date)) {
                return session;
            }
        }
        return null;
    }
}

/**
 * Represents a scheduled session for a course.
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
     * Constructs a session on the specified date.
     *
     * @param date session date
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
     * Assigns a trainer before the session start date. If a trainer already exists, it is replaced.
     *
     * @param t trainer
     * @param today current date
     * @return true if successful; false otherwise
     */
    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || date == null || today == null || !today.isBefore(date)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    /**
     * Replaces the current trainer before the session start date.
     *
     * @param newTrainer new trainer
     * @param today current date
     * @return true if successful; false otherwise
     */
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        return assignTrainer(newTrainer, today);
    }

    /**
     * Registers a participant before the session start date with no duplicates.
     *
     * @param p participant
     * @param today current date
     * @return true if successful; false otherwise
     */
    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || date == null || today == null || !today.isBefore(date)) {
            return false;
        }
        for (Participant participant : registeredParticipants) {
            if (participant != null && Objects.equals(participant.getId(), p.getId())) {
                return false;
            }
        }
        registeredParticipants.add(p);
        return true;
    }
}

/**
 * Represents a sector that groups courses.
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
     * Constructs a sector with the given unique ID.
     *
     * @param id sector id
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
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        courses.remove(course);
    }
}

/**
 * Represents a referenced document.
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
     * Constructs a document with a name.
     *
     * @param name document name
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
 * Base class for persons in the system.
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
     * Constructs a person with the given unique ID.
     *
     * @param id person id
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
 * Represents a participant.
 */
class Participant extends Person {
    /**
     * Unparameterized constructor.
     */
    public Participant() {
        super();
    }

    /**
     * Constructs a participant with the given unique ID.
     *
     * @param id participant id
     */
    public Participant(String id) {
        super(id);
    }
}

/**
 * Represents a trainer.
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
     * Constructs a trainer with the given unique ID and contractor flag.
     *
     * @param id trainer id
     * @param contractor whether the trainer is an independent contractor
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