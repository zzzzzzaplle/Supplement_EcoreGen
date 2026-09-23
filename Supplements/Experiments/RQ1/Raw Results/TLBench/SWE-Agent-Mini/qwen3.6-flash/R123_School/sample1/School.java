import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
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
        if (id == null || id.trim().isEmpty()) {
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

        Sector sector = null;
        if (sectorId != null) {
            for (Sector s : sectors) {
                if (s.getId().equals(sectorId)) {
                    sector = s;
                    break;
                }
            }
            if (sector == null) {
                return false;
            }
        }

        Sector currentSector = course.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(course);
        }

        course.setSector(sector);

        if (sector != null) {
            sector.addCourse(course);
        }

        return true;
    }

    public Sector addSector(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (Sector sector : sectors) {
            if (sector.getId().equals(id)) {
                return sector;
            }
        }
        Sector newSector = new Sector(id);
        sectors.add(newSector);
        return newSector;
    }
}
