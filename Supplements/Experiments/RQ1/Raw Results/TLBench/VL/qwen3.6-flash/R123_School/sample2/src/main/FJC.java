import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;

class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = "";
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
        if (id == null || id.isEmpty()) {
            return false;
        }
        for (Course c : courses) {
            if (c.getId().equals(id)) {
                return false;
            }
        }
        Course newCourse = new Course();
        newCourse.setId(id);
        this.courses.add(newCourse);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourseById(courseId);
        Sector sector = findSectorById(sectorId);

        if (course == null || sector == null) {
            return false;
        }

        // Remove from current sector if assigned
        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        // Assign to new sector
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null; // Already exists
            }
        }
        Sector newSector = new Sector();
        newSector.setId(id);
        this.sectors.add(newSector);
        return newSector;
    }

    private Course findCourseById(String id) {
        for (Course c : courses) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private Sector findSectorById(String id) {
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
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
        if (docs != null) {
            this.documents = new ArrayList<>(docs);
        } else {
            this.documents = new ArrayList<>();
        }
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
        if (date == null) {
            return new ArrayList<>(sessions);
        }

        // Check if session already exists for this date
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                return new ArrayList<>(sessions);
            }
        }

        Session newSession = new Session();
        newSession.setDate(date);
        this.sessions.add(newSession);
        return new ArrayList<>(sessions);
    }

    public boolean cancelSession(LocalDate date) {
        if (date == null) {
            return false;
        }

        Session sessionToRemove = null;
        for (Session s : sessions) {
            if (s.getDate().equals(date)) {
                sessionToRemove = s;
                break;
            }
        }

        if (sessionToRemove == null) {
            return false;
        }

        // Only allow cancellation if no participants
        if (!sessionToRemove.getParticipants().isEmpty()) {
            return false;
        }

        sessions.remove(sessionToRemove);
        return true;
    }

    public boolean addDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        // Check if document already exists
        for (Document d : documents) {
            if (d.equals(doc)) {
                return false;
            }
        }
        this.documents.add(doc);
        return true;
    }

    public boolean removeDocument(Document doc) {
        if (doc == null) {
            return false;
        }
        return this.documents.remove(doc);
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
        if (t == null || today == null || date == null) {
            return false;
        }

        // Must be before session start date
        if (!today.isBefore(date)) {
            return false;
        }

        // Ensure no duplicate trainer assignment if already assigned
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (newTrainer == null || today == null || date == null) {
            return false;
        }

        // Must be before session start date
        if (!today.isBefore(date)) {
            return false;
        }

        // Trainer must already exist to replace
        if (this.trainer == null) {
            return false;
        }

        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (p == null || today == null || date == null) {
            return false;
        }

        // Ensure no duplicate enrollments
        for (Participant existing : registeredParticipants) {
            if (existing.getId().equals(p.getId())) {
                return false;
            }
        }

        this.registeredParticipants.add(p);
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
        if (course != null && !this.courses.contains(course)) {
            this.courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (course != null) {
            this.courses.remove(course);
        }
    }
}

class Document {
    private String name;

    public Document() {
        this.name = "";
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

    public Person() {
        this.id = "";
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
        super();
    }
}

class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
        super();
        this.contractor = false;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}