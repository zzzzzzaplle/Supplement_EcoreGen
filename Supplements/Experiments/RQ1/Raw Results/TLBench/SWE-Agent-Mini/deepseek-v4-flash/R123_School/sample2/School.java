import java.time.LocalDate;
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

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    public boolean addCourse(String id) {
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
        Course courseToAssign = null;
        Sector targetSector = null;

        for (Course course : courses) {
            if (course.getId().equals(courseId)) {
                courseToAssign = course;
                break;
            }
        }

        for (Sector sector : sectors) {
            if (sector.getId().equals(sectorId)) {
                targetSector = sector;
                break;
            }
        }

        if (courseToAssign == null || targetSector == null) {
            return false;
        }

        // Remove from current sector if any
        Sector currentSector = courseToAssign.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(courseToAssign);
        }

        // Add to new sector
        courseToAssign.setSector(targetSector);
        targetSector.addCourse(courseToAssign);
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
