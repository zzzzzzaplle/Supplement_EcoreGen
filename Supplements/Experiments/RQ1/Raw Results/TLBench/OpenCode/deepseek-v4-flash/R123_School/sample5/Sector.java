import java.util.ArrayList;
import java.util.List;

class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
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

    void addCourse(Course course) {
        courses.add(course);
    }

    void removeCourse(Course course) {
        courses.remove(course);
    }
}
