import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                return false;
            }
        }
        Course newCourse = new Course(id);
        this.courses.add(newCourse);
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

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        Sector targetSector = null;
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                targetSector = s;
                break;
            }
        }

        if (targetSector == null) {
            return false;
        }

        course.setSector(targetSector);
        targetSector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null;
            }
        }
        Sector newSector = new Sector(id);
        this.sectors.add(newSector);
        return newSector;
    }
}
