import java.util.ArrayList;
import java.util.List;

public class School {
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

    public List<Sector> getSectors() {
        return sectors;
    }

    public boolean addCourse(String id) {
        if (id == null) {
            return false;
        }
        if (courses == null) {
            courses = new ArrayList<>();
        }
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(id)) {
                return false;
            }
        }
        Course course = new Course(id);
        courses.add(course);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        if (courseId == null || sectorId == null) {
            return false;
        }
        Course foundCourse = null;
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(courseId)) {
                foundCourse = c;
                break;
            }
        }
        if (foundCourse == null) {
            return false;
        }
        Sector foundSector = null;
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(sectorId)) {
                foundSector = s;
                break;
            }
        }
        if (foundSector == null) {
            return false;
        }
        Sector oldSector = foundCourse.getSector();
        if (oldSector != null) {
            oldSector.removeCourse(foundCourse);
        }
        foundCourse.setSector(foundSector);
        foundSector.addCourse(foundCourse);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null) {
            return null;
        }
        if (sectors == null) {
            sectors = new ArrayList<>();
        }
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(id)) {
                return null;
            }
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
