import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = "";
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

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = null;
        Sector sector = null;
        for (Course c : courses) {
            if (courseId != null && courseId.equals(c.getId())) {
                course = c;
                break;
            }
        }
        for (Sector s : sectors) {
            if (sectorId != null && sectorId.equals(s.getId())) {
                sector = s;
                break;
            }
        }
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

    public Sector addSector(String id) {
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
