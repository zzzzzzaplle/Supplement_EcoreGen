import java.util.ArrayList;
import java.util.List;

public class Sector {
    private String id;
    private List<Course> courses = new ArrayList<Course>();

    public Sector() {
    }

    public Sector(String id) {
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

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }
}
