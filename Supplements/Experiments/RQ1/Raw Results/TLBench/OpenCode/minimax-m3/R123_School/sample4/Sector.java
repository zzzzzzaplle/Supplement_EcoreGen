import java.util.ArrayList;
import java.util.List;

public class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.id = "";
        this.courses = new ArrayList<>();
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

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    void addCourse(Course course) {
        if (course == null) {
            return;
        }
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    void removeCourse(Course course) {
        if (course == null) {
            return;
        }
        courses.remove(course);
    }
}
