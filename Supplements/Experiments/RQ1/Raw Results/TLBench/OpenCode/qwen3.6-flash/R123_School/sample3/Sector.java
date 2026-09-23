
import java.time.LocalDate;
import java.util.*;

public class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
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

    void addCourse(Course course) {
        if (courses != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (courses != null) {
            courses.remove(course);
            course.setSector(null);
        }
    }
}
