import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = null;
        this.courses = new ArrayList<Course>();
        this.sectors = new ArrayList<Sector>();
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
        for (Course current : courses) {
            if (current != null && courseId != null && courseId.equals(current.getId())) {
                course = current;
                break;
            }
        }
        if (course == null || sectorId == null) {
            return false;
        }
        Sector sector = null;
        for (Sector current : sectors) {
            if (sectorId.equals(current.getId())) {
                sector = current;
                break;
            }
        }
        if (sector == null) {
            sector = addSector(sectorId);
        }
        if (sector == null) {
            return false;
        }
        Sector previous = course.getSector();
        if (previous != null) {
            previous.removeCourse(course);
        }
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }

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
}
