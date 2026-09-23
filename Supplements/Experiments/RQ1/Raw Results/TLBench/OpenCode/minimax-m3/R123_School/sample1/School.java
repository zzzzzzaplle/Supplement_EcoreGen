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
        if (id == null) {
            return false;
        }
        for (Course c : courses) {
            if (c.getId() != null && c.getId().equals(id)) {
                return false;
            }
        }
        courses.add(new Course(id));
        return true;
    }

    public Sector addSector(String id) {
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = findCourse(courseId);
        Sector sector = findSector(sectorId);
        if (course == null || sector == null) {
            return false;
        }
        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }

    private Course findCourse(String id) {
        if (id == null) {
            return null;
        }
        for (Course c : courses) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    private Sector findSector(String id) {
        if (id == null) {
            return null;
        }
        for (Sector s : sectors) {
            if (id.equals(s.getId())) {
                return s;
            }
        }
        return null;
    }
}
