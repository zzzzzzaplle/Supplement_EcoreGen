import java.util.ArrayList;
import java.util.List;

class Sector {
    private String id;
    private List<Course> courses;

    public Sector() {
        this.id = null;
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
        if (course != null) {
            courses.add(course);
            course.setSector(this);
        }
    }

    void removeCourse(Course course) {
        if (course != null) {
            courses.remove(course);
            course.setSector(null);
        }
    }
}
