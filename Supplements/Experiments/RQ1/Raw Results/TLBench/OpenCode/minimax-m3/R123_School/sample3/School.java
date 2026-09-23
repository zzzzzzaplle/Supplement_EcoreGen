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
            if (id.equals(c.getId())) {
                return false;
            }
        }
        Course newCourse = new Course(id);
        courses.add(newCourse);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null) {
            return null;
        }
        for (Sector s : sectors) {
            if (id.equals(s.getId())) {
                return s;
            }
        }
        Sector newSector = new Sector(id);
        sectors.add(newSector);
        return newSector;
    }

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
        Sector sector = null;
        for (Sector s : sectors) {
            if (sectorId.equals(s.getId())) {
                sector = s;
                break;
            }
        }
        if (sector == null) {
            return false;
        }
        Sector current = course.getSector();
        if (current != null) {
            current.removeCourse(course);
        }
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }
}
