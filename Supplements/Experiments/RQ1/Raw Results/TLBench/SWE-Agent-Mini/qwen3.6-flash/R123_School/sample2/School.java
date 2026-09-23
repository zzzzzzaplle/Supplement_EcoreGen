import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class School {
    private String name;
    private List<Course> courses;
    private List<Sector> sectors;
    private Map<String, Course> courseMap;
    private Map<String, Sector> sectorMap;

    public School() {
        this("");
    }

    public School(String name) {
        this.name = name;
        this.courses = new ArrayList<>();
        this.sectors = new ArrayList<>();
        this.courseMap = new HashMap<>();
        this.sectorMap = new HashMap<>();
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
        if (courseMap.containsKey(id)) {
            return false;
        }
        Course course = new Course(id);
        courses.add(course);
        courseMap.put(id, course);
        return true;
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course course = courseMap.get(courseId);
        Sector sector = sectorMap.get(sectorId);
        if (course == null || sector == null) {
            return false;
        }
        // Remove from current sector if any
        if (course.getSector() != null) {
            course.getSector().removeCourse(course);
        }
        // Add to new sector
        sector.addCourse(course);
        course.setSector(sector);
        return true;
    }

    public Sector addSector(String id) {
        if (sectorMap.containsKey(id)) {
            return null;
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        sectorMap.put(id, sector);
        return sector;
    }
}
