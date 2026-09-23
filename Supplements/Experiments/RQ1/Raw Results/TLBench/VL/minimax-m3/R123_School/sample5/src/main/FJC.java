import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(id)) {
                return false;
            }
        }
        courses.add(new Course(id));
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = null;
        Sector targetSector = null;
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(sectorId)) {
                targetSector = s;
                break;
            }
        }
        if (course == null || targetSector == null) {
            return false;
        }
        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }
        targetSector.addCourse(course);
        course.setSector(targetSector);
        return true;
    }

    public Sector addSector(String id) {
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

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Session> addSession(LocalDate date) {
        for (Session s : sessions) {
            if (s.getDate() != null && s.getDate().equals(date)) {
                return sessions;
            }
        }
        sessions.add(new Session(date));
        return sessions;
    }

    public boolean cancelSession(LocalDate date) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getDate() != null && sessions.get(i).getDate().equals(date)) {
                if (sessions.get(i).getParticipants().isEmpty()) {
                    sessions.remove(i);
                    return true;
                }
                return false;
            }
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

    public void setRegisteredParticipants(List<Participant> registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }

    public boolean assignTrainer(Trainer t, LocalDate today) {
        if (date == null || today == null) {
            return false;
        }
        if (!date.isAfter(today)) {
            return false;
        }
        this.trainer = t;
        return true;
    }

    public boolean replaceTrainer(Trainer newTrainer, LocalDate today) {
        if (date == null || today == null) {
            return false;
        }
        if (!date.isAfter(today)) {
            return false;
        }
        this.trainer = newTrainer;
        return true;
    }

    public boolean registerParticipant(Participant p, LocalDate today) {
        if (date == null || today == null) {
            return false;
        }
        if (date.isBefore(today)) {
            return false;
        }
        if (registeredParticipants.contains(p)) {
            return false;
        }
        registeredParticipants.add(p);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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