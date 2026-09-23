import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;

    public School() {
        this.courses = new ArrayList<Course>();
        this.sectors = new ArrayList<Sector>();
    }

    public School(String name) {
        this.name = name;
        this.courses = new ArrayList<Course>();
        this.sectors = new ArrayList<Sector>();
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
            if (course != null && id != null && id.equals(course.getId())) return false;
        }
        courses.add(new Course(id));
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = null;
        Sector sector = null;
        for (Course c : courses) {
            if (c != null && courseId != null && courseId.equals(c.getId())) course = c;
        }
        for (Sector s : sectors) {
            if (s != null && sectorId != null && sectorId.equals(s.getId())) sector = s;
        }
        if (course == null || sector == null) return false;
        Sector current = course.getSector();
        if (current != null) current.removeCourse(course);
        course.setSector(sector);
        sector.addCourse(course);
        return true;
    }

    public Sector addSector(String id) {
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
