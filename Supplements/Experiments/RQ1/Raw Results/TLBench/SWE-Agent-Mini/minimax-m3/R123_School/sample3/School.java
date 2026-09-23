import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.name = "";
        this.courses = new ArrayList<Course>();
        this.sectors = new ArrayList<Sector>();
    }

    public School(String name) {
        this.name = name;
        this.courses = new ArrayList<Course>();
        this.sectors = new ArrayList<Sector>();
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
        Course newCourse = new Course(id);
        courses.add(newCourse);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null) {
            return null;
        }
        for (Sector s : sectors) {
            if (s.getId() != null && s.getId().equals(id)) {
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
        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }
}
