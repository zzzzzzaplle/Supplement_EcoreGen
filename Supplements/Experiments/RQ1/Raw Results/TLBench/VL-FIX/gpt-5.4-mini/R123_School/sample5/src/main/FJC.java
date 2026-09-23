import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a school that manages courses, sectors, and educational sessions.
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
     * Constructs a school with the given name.
     *
     * @param name the unique school name
     */
    public School(String name) {
        this();
        this.name = name;
    }

    /**
     * Gets the school name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the school name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of courses.
     *
     * @return the courses
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Sets the list of courses.
     *
     * @param courses the courses
     */
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * Gets the list of sectors.
     *
     * @return the sectors
     */
    public List<Sector> getSectors() {
        return sectors;
    }

    /**
     * Sets the list of sectors.
     *
     * @param sectors the sectors
     */
    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    /**
     * Adds a new course with a case-sensitive unique ID.
     *
     * @param id the course ID
     * @return true if added successfully; otherwise false
     */
    public boolean addCourse(String id) {
        if (id == null) {
            return false;
        }
        for (Course course : courses) {
            if (id.equals(course.getId())) {
                return false;
            }
        }
        courses.add(new Course(id));
        return true;
    }

    /**
     * Adds a sector if not already present.
     *
     * @param id the sector ID
     * @return the created sector, or the existing sector if already present
     */
    public Sector addSector(String id) {
        if (id == null) {
            return null;
        }
        for (Sector sector : sectors) {
            if (id.equals(sector.getId())) {
                return sector;
            }
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }

    /**
     * Assigns a course to a sector, removing it from any previous sector first.
     *
     * @param courseId the course ID
     * @param sectorId the sector ID
     * @return true if successful; otherwise false
     */
    public boolean assignCourseToSector(String courseId, String sectorId) {
        if (courseId == null || sectorId == null) {
            return false;
        }

        Course course = null;
        for (Course c : courses) {
            if (courseId.equals(c.getId())) {
                course = c;
                break;
            }
        }
        if (course == null) {
            return false;
        }

        Sector targetSector = null;
        for (Sector s : sectors) {
            if (sectorId.equals(s.getId())) {
                targetSector = s;
                break;
            }
        }
        if (targetSector == null) {
            return false;
        }

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        course.setSector(targetSector);
        targetSector.addCourse(course);
        return true;
    }
}

/**
 * Represents a course that may contain documents and sessions.
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
     * Constructs a course with the given ID.
     *
     * @param id the course ID
     */
    public Course(String id) {
        this();
        this.id = id;
    }

    /**
     * Gets the course ID.
     *
     * @return the course ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the course ID.
     *
     * @param id the new course ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the list of documents.
     *
     * @return the documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Sets the list of documents.
     *
     * @param docs the documents
     */
    public void setDocuments(List<Document> docs) {
        this.documents = docs;
    }

    /**
     * Gets the sector.
     *
     * @return the sector
     */
    public Sector getSector() {
        return sector;
    }

    /**
     * Sets the sector.
     *
     * @param s the sector
     */
    public void setSector(Sector s) {
        this.sector = s;
    }

    /**
     * Gets the sessions.
     *
     * @return the sessions
     */
    public List<Session> getSessions() {
        return sessions;
    }

    /**
     * Sets the sessions.
     *
     * @param sessions the sessions
     */
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    /**
     * Adds a session on the given date if one does not already exist for that day.
     *
     * @param date the session date
     * @return the up-to-date list of sessions
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
     * Cancels a session on the given date, only if it has no participants.
     *
     * @param date the session date
     * @return true if successfully canceled; otherwise false
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
     * Adds a document if it does not already exist.
     *
     * @param doc the document to add
     * @return true if added successfully; otherwise false
     */
    public boolean addDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        if (!documents.contains(doc)) {
            documents.add(doc);
            return true;
        }
        return false;
    }

    /**
     * Removes a document if present.
     *
     * @param doc the document to remove
     * @return true if removed successfully; otherwise false
     */
    public boolean removeDocument(Document doc) {
        return documents.remove(doc);
    }
}

/**
 * Represents a session scheduled for a course.
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
     * Constructs a session on the given date.
     *
     * @param date the session date
     */
    public Session(LocalDate date) {
        this();
        this.date = date;
    }

    /**
     * Gets the session date.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the session date.
     *
     * @param date the new date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the assigned trainer.
     *
     * @return the trainer
     */
    public Trainer getTrainer() {
        return trainer;
    }

    /**
     * Sets the trainer.
     *
     * @param trainer the trainer
     */
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    /**
     * Gets the registered participants.
     *
     * @return the participants
     */
    public List<Participant> getParticipants() {
        return registeredParticipants;
    }

    /**
     * Sets the registered participants.
     *
     * @param registeredParticipants the participants
     */
    public void setParticipants(List<Participant> registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }

    /**
     * Assigns a trainer before the session starts.
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
     * Replaces the trainer before the session starts.
     *
     * @param newTrainer the new trainer
     * @param today the current date
     * @return true if successful; otherwise false
     */
    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        return assignTrainer(newTrainer, today);
    }

    /**
     * Registers a participant for the session before it starts.
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
     * Constructs a sector with the given ID.
     *
     * @param id the sector ID
     */
    public Sector(String id) {
        this();
        this.id = id;
    }

    /**
     * Gets the sector ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the sector ID.
     *
     * @param id the new ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the courses in this sector.
     *
     * @return the courses
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Sets the courses list.
     *
     * @param courses the courses
     */
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * Adds a course to this sector.
     *
     * @param course the course
     */
    void addCourse(Course course) {
        if (course == null) {
            return;
        }
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    /**
     * Removes a course from this sector.
     *
     * @param course the course
     */
    void removeCourse(Course course) {
        courses.remove(course);
    }
}

/**
 * Represents a referenced document for a course.
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
     * @param name the document name
     */
    public Document(String name) {
        this.name = name;
    }

    /**
     * Gets the document name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the document name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compares documents by name.
     *
     * @param o the other object
     * @return true if equal; otherwise false
     */
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

    /**
     * Returns a hash code based on the document name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

/**
 * Abstract base class for people in the school system.
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
     * Constructs a person with the given ID.
     *
     * @param id the person ID
     */
    public Person(String id) {
        this.id = id;
    }

    /**
     * Gets the person ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the person ID.
     *
     * @param id the new ID
     */
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
     * Constructs a participant with the given ID.
     *
     * @param id the participant ID
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
     * Constructs a trainer with the given ID and contractor status.
     *
     * @param id the trainer ID
     * @param contractor whether the trainer is an individual contractor
     */
    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    /**
     * Indicates whether the trainer is an individual contractor.
     *
     * @return true if contractor; otherwise false
     */
    public boolean isContractor() {
        return contractor;
    }

    /**
     * Sets the contractor status.
     *
     * @param contractor the new status
     */
    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}