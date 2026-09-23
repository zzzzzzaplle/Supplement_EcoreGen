import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
    }

    public School(String name) {
        this();
        this.name = name;
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
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                return false;
            }
        }
        Course newCourse = new Course(id);
        courses.add(newCourse);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Optional<Course> courseOpt = courses.stream()
                .filter(c -> c.getId().equals(courseId))
                .findFirst();
        if (!courseOpt.isPresent()) {
            return false;
        }
        Course course = courseOpt.get();
        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }
        Sector sector = null;
        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                sector = s;
                break;
            }
        }
        if (sector == null) {
            return false;
        }
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }

    public Sector addSector(String id) {
        for (Sector sector : sectors) {
            if (sector.getId().equals(id)) {
                return null;
            }
        }
        Sector newSector = new Sector(id);
        sectors.add(newSector);
        return newSector;
    }
}
