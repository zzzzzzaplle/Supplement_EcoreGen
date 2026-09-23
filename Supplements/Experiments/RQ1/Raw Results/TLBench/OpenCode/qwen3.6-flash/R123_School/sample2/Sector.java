import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.courses = new ArrayList<>();
    }

    public Sector(String id) {
        this();
        this.id = id;
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
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (course != null) {
            courses.remove(course);
        }
    }
}
