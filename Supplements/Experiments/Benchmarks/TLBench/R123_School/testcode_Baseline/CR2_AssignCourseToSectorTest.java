import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.time.LocalDate;

public class CR2_AssignCourseToSectorTest {

    @Test
    public void tc1_FirstCourseForASector() {
        // Setup
        School school = new School();
        school.setName("S1");
        Sector sector = school.addSector("SE01");
        school.addCourse("CS201");

        // Testing: assignCourseToSector(String courseId, String sectorId): boolean
        boolean result = school.assignCourseToSector("CS201", "SE01");

        // Assertions
        assertTrue("Assignment should succeed", result);
        assertEquals("Sector should contain 1 course", 1, sector.getCourses().size());
        assertEquals("Course should reference correct sector",
                sector, school.getCourses().get(0).getSector());
    }

    @Test
    public void tc2_AttemptToMoveAlreadyAssignedCourse() {
        // Setup
        School school = new School();
        school.setName("S1");
        Sector sector1 = school.addSector("SE01");
        Sector sector2 = school.addSector("SE02");
        school.addCourse("CS202");

        // Initial Assignment
        school.assignCourseToSector("CS202", "SE01");

        // Testing: Reassignment
        boolean result = school.assignCourseToSector("CS202", "SE02");

        // Assertions
        assertTrue("Reassignment should succeed", result);
        assertEquals("Target sector should have 1 course", 1, sector2.getCourses().size());
        assertEquals("Original sector should have 0 courses", 0, sector1.getCourses().size());
        assertEquals("Course should reference new sector",
                sector2, school.getCourses().get(0).getSector());
    }

    @Test
    public void tc3_AttemptWithNonExistentCourse() {
        // Setup
        School school = new School();
        school.setName("S1");
        Sector sector = school.addSector("SE03");

        // Testing: Invalid course assignment
        boolean result = school.assignCourseToSector("Invalid", "SE03");

        // Assertions
        assertFalse("Assignment should fail", result);
        assertEquals("Sector should remain empty", 0, sector.getCourses().size());
    }

    @Test
    public void tc4_InvalidSectorAssignment() {
        // Setup
        School school = new School();
        school.setName("S1");
        school.addCourse("CS204");

        // Testing: Invalid sector assignment
        boolean result = school.assignCourseToSector("CS204", "Invalid");

        // Assertions
        assertFalse("Assignment should fail", result);
        assertNull("Course should have no sector",
                school.getCourses().get(0).getSector());
    }

    @Test
    public void tc5_MultipleCoursesInSameSector() {
        // Setup
        School school = new School();
        school.setName("S1");
        Sector sector = school.addSector("SE05");
        school.addCourse("CS201");
        school.addCourse("CS202");
        school.addCourse("CS205");

        // Assign courses
        school.assignCourseToSector("CS201", "SE05");
        school.assignCourseToSector("CS202", "SE05");
        boolean result = school.assignCourseToSector("CS205", "SE05");

        // Assertions
        assertTrue("Assignment should succeed", result);
        assertEquals("Sector should contain 3 courses", 3, sector.getCourses().size());
        assertTrue("All courses should reference the sector",
                school.getCourses().stream()
                        .allMatch(c -> sector.equals(c.getSector())));
    }
}

/*
compile_result:



run_result:
JUnit version 4.13.2
.....
Time: 0.012

OK (5 tests)


*/