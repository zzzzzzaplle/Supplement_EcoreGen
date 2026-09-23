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
     * @param name unique school name
     */
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

    /**
     * Adds a new course with the provided unique ID.
     *
     * @param id case-sensitive unique course identifier
     * @return true if added successfully, false otherwise
     */
    public boolean addCourse(String id) {
        if (id == null || id.isBlank() || findCourseById(id) != null) {
            return false;
        }
        courses.add(new Course(id));
        return true;
    }

    /**
     * Adds a new sector with the provided unique ID.
     *
     * @param id unique sector identifier
     * @return the added sector, or the existing matching sector if already present
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

    /**
     * Assigns a course to a sector, removing it from any previous sector first.
     *
     * @param courseId course identifier
     * @param sectorId sector identifier
     * @return true if successful, false otherwise
     */
    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourseById(courseId);
        Sector targetSector = findSectorById(sectorId);

        if (course == null || targetSector == null) {
            return false;
        }

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        targetSector.addCourse(course);
        course.setSector(targetSector);
        return true;
    }

    private Course findCourseById(String id) {
        if (id == null) {
            return null;
        }
        for (Course course : courses) {
            if (id.equals(course.getId())) {
                return course;
            }
        }
        return null;
    }

    private Sector findSectorById(String id) {
        if (id == null) {
            return null;
        }
        for (Sector sector : sectors) {
            if (id.equals(sector.getId())) {
                return sector;
            }
        }
        return null;
    }
}

/**
 * Represents a course with documents, sessions, and a sector association.
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
     * Constructs a course with the given unique identifier.
     *
     * @param id unique course identifier
     */
    public Course(String id) {
        this.id = id;
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
     * Adds a session for the specified date, ensuring only one session per day.
     *
     * @param date session date
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
     * Cancels a session only if it exists and has no participants.
     *
     * @param date session date
     * @return true if removed, false otherwise
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
     * Adds a document to the course, avoiding duplicates by document equality.
     *
     * @param doc document to add
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
     * Removes a document from the course.
     *
     * @param doc document to remove
     * @return true if removed, false otherwise
     */
    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}

/**
 * Represents a session of a course.
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
     * Constructs a session with the given date.
     *
     * @param date session date
     */
    public Session(LocalDate date) {
        this.date = date;
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

    /**
     * Assigns a trainer before the session date if none exists.
     *
     * @param t trainer to assign
     * @param today current date
     * @return true if successful, false otherwise
     */
    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (t == null || today == null || date == null || !today.isBefore(date)) {
            return false;
        }
        if (this.trainer != null) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    /**
     * Replaces the trainer before the session date.
     *
     * @param newTrainer new trainer to assign
     * @param today current date
     * @return true if successful, false otherwise
     */
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || date == null || !today.isBefore(date)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    /**
     * Registers a participant before the session date, preventing duplicates.
     *
     * @param p participant to register
     * @param today current date
     * @return true if successful, false otherwise
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
 * Represents a sector grouping courses.
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
     * Constructs a sector with the given unique identifier.
     *
     * @param id unique sector identifier
     */
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
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        courses.remove(course);
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
     * Constructs a document with the given name.
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        Document document = (Document) o;
        return Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

/**
 * Base abstract class for people in the school system.
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
     * Constructs a person with the given unique identifier.
     *
     * @param id person identifier
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
     * Constructs a participant with the given identifier.
     *
     * @param id participant identifier
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
     * Constructs a trainer with the given identifier and contractor status.
     *
     * @param id trainer identifier
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