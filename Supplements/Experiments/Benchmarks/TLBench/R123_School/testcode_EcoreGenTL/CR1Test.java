package edu.trainingSchool.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.trainingSchool.TrainingSchoolFactory;
import edu.trainingSchool.School;
import edu.trainingSchool.Course;

public class CR1Test {

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

    // ---- CR1: Add a course ----

    /**
     * Test Case 1: "Fresh identifier"
     * Setup:
     *   1. Create school S1.
     * Input: Add course CR101 to school S1.
     * Expected Output: True
     */
    @Test
    public void testCase1_FreshIdentifier() {
        // Setup
        School school = createSchool("S1");

        // Action
        boolean result = school.addCourse("CR101");

        // Expected: True
        assertTrue("Should successfully add course with fresh identifier", result);
        assertEquals("School should have 1 course", 1, school.getCourses().size());
        assertEquals("Course id should be CR101", "CR101", school.getCourses().get(0).getId());
    }

    /**
     * Test Case 2: "Duplicate identifier"
     * Setup:
     *   1. Create school S1.
     *   2. Add course CR101 to school S1.
     * Input: Add course CR101 to school S1 again.
     * Expected Output: False
     */
    @Test
    public void testCase2_DuplicateIdentifier() {
        // Setup
        School school = createSchool("S1");
        school.addCourse("CR101");

        // Action: add CR101 again
        boolean result = school.addCourse("CR101");

        // Expected: False
        assertFalse("Should not add course with duplicate identifier", result);
        assertEquals("School should still have 1 course", 1, school.getCourses().size());
    }

    /**
     * Test Case 3: "Second unique identifier"
     * Setup:
     *   1. Create school S1.
     *   2. Catalog already lists CR101 only.
     * Input: Add course CR102 to school S1.
     * Expected Output: True
     */
    @Test
    public void testCase3_SecondUniqueIdentifier() {
        // Setup
        School school = createSchool("S1");
        school.addCourse("CR101");

        // Action
        boolean result = school.addCourse("CR102");

        // Expected: True
        assertTrue("Should successfully add course with second unique identifier", result);
        assertEquals("School should have 2 courses", 2, school.getCourses().size());
    }

    /**
     * Test Case 4: "Identifier case clash"
     * Setup:
     *   1. Create school S1.
     *   2. Catalog already lists CR101 (upper-case).
     * Input: Add course cr101 (lower-case) to school S1.
     * Expected Output: True (case-sensitive comparison)
     */
    @Test
    public void testCase4_IdentifierCaseClash() {
        // Setup
        School school = createSchool("S1");
        school.addCourse("CR101");

        // Action: add cr101 (lower-case)
        boolean result = school.addCourse("cr101");

        // Expected: True (case-sensitive, "cr101" != "CR101")
        assertTrue("Should add course with different case identifier (case-sensitive)", result);
        assertEquals("School should have 2 courses", 2, school.getCourses().size());
    }

    /**
     * Test Case 5: "Long alphanumeric identifier"
     * Setup:
     *   1. Create school S1.
     *   2. Catalog lists CR101 and CR102.
     * Input: Add course CR-ADV-2025 to school S1.
     * Expected Output: True
     */
    @Test
    public void testCase5_LongAlphanumericIdentifier() {
        // Setup
        School school = createSchool("S1");
        school.addCourse("CR101");
        school.addCourse("CR102");

        // Action
        boolean result = school.addCourse("CR-ADV-2025");

        // Expected: True
        assertTrue("Should successfully add course with long alphanumeric identifier", result);
        assertEquals("School should have 3 courses", 3, school.getCourses().size());
    }
}
