package edu.trainingSchool.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.trainingSchool.TrainingSchoolFactory;
import edu.trainingSchool.School;
import edu.trainingSchool.Course;
import edu.trainingSchool.Sector;

public class CR2Test {

    private TrainingSchoolFactory factory;

    @Before
    public void setUp() {
        factory = TrainingSchoolFactory.eINSTANCE;
    }

    // ---- Helper methods ----

    private School createSchool(String name) {
        School school = factory.createSchool();
        school.setName(name);
        return school;
    }

    private Sector createSector(School school, String id) {
        return school.addSector(id);
    }

    // ---- CR2: Assign a course to a sector ----

    /**
     * Test Case 1: "First course for a sector"
     * Setup:
     *   1. Create school S11.
     *   2. Add sector SE01 to S11.
     *   3. Add course CS201 to S11.
     * Input: Assign course CS201 to sector SE01 in school S11.
     * Expected Output:
     *   - True
     *   - SE01: 1 course
     */
    @Test
    public void testCase1_FirstCourseForSector() {
        // Setup
        School school = createSchool("S11");
        Sector se01 = createSector(school, "SE01");
        school.addCourse("CS201");

        // Action
        boolean result = school.assignCourseToSector("CS201", "SE01");

        // Expected: True, SE01 has 1 course
        assertTrue("Should successfully assign course to sector", result);
        assertEquals("SE01 should have 1 course", 1, se01.getCourses().size());
    }

    /**
     * Test Case 2: "Attempt to move an already-assigned course"
     * Setup:
     *   1. Create school S12.
     *   2. Add sectors SE01 and SE02.
     *   3. Add course CS202.
     *   4. Assign CS202 to SE01 (True), SE01 has 1 course.
     * Input: Assign course CS202 to sector SE02 in school S12.
     * Expected Output:
     *   - True
     *   - SE02: 1 course
     *   - SE01: 0 courses
     */
    @Test
    public void testCase2_MoveAlreadyAssignedCourse() {
        // Setup
        School school = createSchool("S12");
        Sector se01 = createSector(school, "SE01");
        Sector se02 = createSector(school, "SE02");
        school.addCourse("CS202");
        school.assignCourseToSector("CS202", "SE01");
        assertEquals("SE01 should have 1 course before move", 1, se01.getCourses().size());

        // Action: assign CS202 to SE02
        boolean result = school.assignCourseToSector("CS202", "SE02");

        // Expected: True, SE02 has 1 course, SE01 has 0 courses
        assertTrue("Should successfully move course to new sector", result);
        assertEquals("SE02 should have 1 course", 1, se02.getCourses().size());
        assertEquals("SE01 should have 0 courses after move", 0, se01.getCourses().size());
    }

    /**
     * Test Case 3: "Attempt with non-existent course"
     * Setup:
     *   1. Create school S13.
     *   2. Add sector SE03.
     * Input: Assign "Invalid" to sector SE03 in school S13.
     * Expected Output:
     *   - False
     *   - SE03: 0 courses
     */
    @Test
    public void testCase3_NonExistentCourse() {
        // Setup
        School school = createSchool("S13");
        Sector se03 = createSector(school, "SE03");

        // Action: assign non-existent course "Invalid"
        boolean result = school.assignCourseToSector("Invalid", "SE03");

        // Expected: False, SE03 has 0 courses
        assertFalse("Should return false for non-existent course", result);
        assertEquals("SE03 should have 0 courses", 0, se03.getCourses().size());
    }

    /**
     * Test Case 4: "Invalid sector assignment"
     * Setup:
     *   1. Create school S14.
     *   2. Add course CS204.
     * Input: Assign course CS204 to "Invalid" non-existent sector.
     * Expected Output:
     *   - False
     */
    @Test
    public void testCase4_InvalidSector() {
        // Setup
        School school = createSchool("S14");
        school.addCourse("CS204");

        // Action: assign to non-existent sector
        boolean result = school.assignCourseToSector("CS204", "Invalid");

        // Expected: False
        assertFalse("Should return false for non-existent sector", result);
    }

    /**
     * Test Case 5: "Multiple courses in same sector"
     * Setup:
     *   1. Create school S15.
     *   2. Add sector SE05.
     *   3. Add courses CS201 and CS202, assign both to SE05.
     *   4. Add course CS205.
     * Input: Assign course CS205 to sector SE05 in school S15.
     * Expected Output:
     *   - True
     *   - SE05: 3 courses
     */
    @Test
    public void testCase5_MultipleCoursesInSameSector() {
        // Setup
        School school = createSchool("S15");
        Sector se05 = createSector(school, "SE05");
        school.addCourse("CS201");
        school.addCourse("CS202");
        school.assignCourseToSector("CS201", "SE05");
        school.assignCourseToSector("CS202", "SE05");
        assertEquals("SE05 should have 2 courses before adding third", 2, se05.getCourses().size());
        school.addCourse("CS205");

        // Action: assign CS205 to SE05
        boolean result = school.assignCourseToSector("CS205", "SE05");

        // Expected: True, SE05 has 3 courses
        assertTrue("Should successfully assign third course to sector", result);
        assertEquals("SE05 should have 3 courses", 3, se05.getCourses().size());
    }
}
