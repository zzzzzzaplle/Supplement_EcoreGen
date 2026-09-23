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
        if (id == null) return false;
        if (courses == null) {
            courses = new ArrayList<>();
        }
        for (Course c : courses) {
            if (id.equals(c.getId())) {
                return false;
            }
        }
        courses.add(new Course(id));
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        if (courseId == null || sectorId == null) return false;
        if (courses == null || sectors == null) return false;

        Course course = null;
        for (Course c : courses) {
            if (courseId.equals(c.getId())) {
                course = c;
                break;
            }
        }
        if (course == null) return false;

        Sector sector = null;
        for (Sector s : sectors) {
            if (sectorId.equals(s.getId())) {
                sector = s;
                break;
            }
        }
        if (sector == null) return false;

        Sector current = course.getSector();
        if (current != null) {
            current.removeCourse(course);
        }
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null) return null;
        if (sectors == null) {
            sectors = new ArrayList<>();
        }
        for (Sector s : sectors) {
            if (id.equals(s.getId())) {
                return null;
            }
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
