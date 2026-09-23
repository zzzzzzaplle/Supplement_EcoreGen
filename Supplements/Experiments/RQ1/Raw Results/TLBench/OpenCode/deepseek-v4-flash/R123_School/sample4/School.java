import java.util.ArrayList;
import java.util.List;

class School {
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
        for (Course c : courses) {
            if (c.getId().equals(id)) {
                return false;
            }
        }
        return courses.add(new Course(id));
    }

    public boolean assignCourseToSector(String courseId, String sectorId) {
        Course targetCourse = null;
        Sector targetSector = null;

        for (Course c : courses) {
            if (c.getId().equals(courseId)) {
                targetCourse = c;
                break;
            }
        }

        for (Sector s : sectors) {
            if (s.getId().equals(sectorId)) {
                targetSector = s;
                break;
            }
        }

        if (targetCourse == null || targetSector == null) {
            return false;
        }

        Sector currentSector = targetCourse.getSector();
        if (currentSector != null) {
            currentSector.removeCourse(targetCourse);
        }

        targetCourse.setSector(targetSector);
        targetSector.addCourse(targetCourse);
        return true;
    }

    public Sector addSector(String id) {
        for (Sector s : sectors) {
            if (s.getId().equals(id)) {
                return null;
            }
        }
        Sector sector = new Sector(id);
        sectors.add(sector);
        return sector;
    }
}
