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
        if (id == null || id.isEmpty()) {
            return false;
        }
        for (Course c : courses) {
            if (c.getId().equals(id)) {
                return false;
            }
        }
        Course course = new Course(id);
        courses.add(course);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = null;
        for (Course c : courses) {
            if (c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }
        if (course == null) {
            return false;
        }

        Sector newSector = null;
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                newSector = s;
                break;
            }
        }
        if (newSector == null) {
            return false;
        }

        // Remove from current sector if any
        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        course.setSector(newSector);
        newSector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null;
            }
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
