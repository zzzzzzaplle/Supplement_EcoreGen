import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a school that manages courses, sectors, and related operations.
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
     * Constructs a school with a unique name.
     *
     * @param name the school name
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
     * Adds a new course by case-sensitive unique ID.
     *
     * @param id course id
     * @return true if added successfully, false otherwise
     */
    public boolean addCourse(String id) {
        if (id == null || id.isEmpty() || findCourseById(id) != null) {
            return false;
        }
        courses.add(new Course(id));
        return true;
    }

    /**
     * Assigns a course to a sector. Removes the course from its current sector first, if any.
     *
     * @param courseId course id
     * @param sectorId  sector id
     * @return true if successful, false otherwise
     */
    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourseById(courseId);
        Sector sector = findSectorById(sectorId);
        if (course == null || sector == null) {
            return false;
        }

        Sector current = course.getSector();
        if (current != null) {
            current.removeCourse(course);
        }

        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    /**
     * Adds a new sector if one with the same id does not exist.
     *
     * @param id sector id
     * @return the created sector, or existing sector if already present
     */
    public Sector addSector(String id) {
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
 * Represents a course that contains documents and scheduled sessions.
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
     * Constructs a course with an identifier.
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
        this.documents = (docs == null) ? new ArrayList<>() : new ArrayList<>(docs);
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
     * Adds a session on the given date if none exists for that day.
     *
     * @param date session date
     * @return the up-to-date list of sessions
     */
    public List<Session> addSession(LocalDate date) {
        if (date == null) {
            return Collections.unmodifiableList(sessions);
        }
        if (findSessionByDate(date) == null) {
            sessions.add(new Session(date));
        }
        return Collections.unmodifiableList(sessions);
    }

    /**
     * Cancels a session only if it has no participants.
     *
     * @param date session date
     * @return true if removed, false otherwise
     */
    public boolean cancelSession(LocalDate date) {
        Session session = findSessionByDate(date);
        if (session == null || !session.getParticipants().isEmpty()) {
            return false;
        }
        return sessions.remove(session);
    }

    /**
     * Adds a document if not already present.
     *
     * @param doc document
     * @return true if added, false otherwise
     */
    public boolean addDocument(Document doc) {
        if (doc == null || documents.contains(doc)) {
            return false;
        }
        documents.add(doc);
        return true;
    }

    /**
     * Removes a document if present.
     *
     * @param doc document
     * @return true if removed, false otherwise
     */
    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }

    private Session findSessionByDate(LocalDate date) {
        for (Session session : sessions) {
            if (Objects.equals(session.getDate(), date)) {
                return session;
            }
        }
        return null;
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
     * Constructs a session with a date.
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
     * Assigns a trainer before the session start date.
     *
     * @param t     trainer
     * @param today current date
     * @return true if successful, false otherwise
     */
    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || date == null || today == null || !today.isBefore(date) && !today.equals(date)) {
            return false;
        }
        if (today.isBefore(date) || today.equals(date.minusDays(0))) {
            this.trainer = t;
            return true;
        }
        return false;
    }

    /**
     * Replaces the trainer before the session start date.
     *
     * @param newTrainer new trainer
     * @param today current date
     * @return true if successful, false otherwise
     */
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || date == null || today == null || !today.isBefore(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    /**
     * Registers a participant before the session starts, ensuring no duplicate enrollments.
     *
     * @param p     participant
     * @param today current date
     * @return true if successful, false otherwise
     */
    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || date == null || today == null || !today.isBefore(date)) {
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
     * Constructs a sector with an identifier.
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
            course.setSector(this);
        }
    }

    void removeCourse(Course course) {
        if (course != null) {
            courses.remove(course);
            if (course.getSector() == this) {
                course.setSector(null);
            }
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
     * Constructs a document with a name.
     *
     * @param name document name
     */
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

/**
 * Abstract base class for people in the system.
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
     * Constructs a person with an identifier.
     *
     * @param id person id
     */
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

/**
 * Represents a participant that can register for sessions.
 */
class Participant extends Person {

    /**
     * Unparameterized constructor.
     */
    public Participant() {
        super();
    }

    /**
     * Constructs a participant with an identifier.
     *
     * @param id participant id
     */
    public Participant(String id) {
        super(id);
    }
}

/**
 * Represents a trainer who can lead sessions.
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
     * Constructs a trainer with an identifier and contractor flag.
     *
     * @param id         trainer id
     * @param contractor  whether the trainer is an independent contractor
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