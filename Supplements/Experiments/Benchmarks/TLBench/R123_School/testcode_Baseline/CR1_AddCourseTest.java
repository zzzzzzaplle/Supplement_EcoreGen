import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class CR1_AddCourseTest {

    @Test
    public void testAddCourseWithFreshIdentifier() {
        // Test Case 1: "Fresh identifier"
        // Setup
        School school = new School();
        school.setName("S1");

        // Input: Add course CR101 to school S1
        boolean result = school.addCourse("CR101");

        // Expected Output: True
        assertTrue("Adding a fresh course identifier should return true", result);

        // Validation
        List<Course> courses = school.getCourses();
        assertEquals("Course count should be 1", 1, courses.size());
        assertEquals("Course ID mismatch", "CR101", courses.get(0).getId());
    }

    @Test
    public void testAddCourseWithDuplicateIdentifier() {
        // Test Case 2: "Duplicate identifier"
        // Setup
        School school = new School();
        school.setName("S1");
        school.addCourse("CR101");

        // Input: Add course CR101 to school S1 again
        boolean result = school.addCourse("CR101");

        // Expected Output: False
        assertFalse("Adding a duplicate course identifier should return false", result);

        // Validation
        List<Course> courses = school.getCourses();
        assertEquals("Course count should remain 1 on duplicate add", 1, courses.size());
    }

    @Test
    public void testAddCourseWithSecondUniqueIdentifier() {
        // Test Case 3: "Second unique identifier"
        // Setup
        School school = new School();
        school.setName("S1");
        school.addCourse("CR101");

        // Input: Add course CR102 to school S1
        boolean result = school.addCourse("CR102");

        // Expected Output: True
        assertTrue("Adding a second unique course identifier should return true", result);

        // Validation
        List<Course> courses = school.getCourses();
        assertEquals("Course count should be 2", 2, courses.size());
        assertEquals("First course ID mismatch", "CR101", courses.get(0).getId());
        assertEquals("Second course ID mismatch", "CR102", courses.get(1).getId());
    }

    @Test
    public void testAddCourseWithIdentifierCaseClash() {
        // Test Case 4: "Identifier case clash"
        // Setup
        School school = new School();
        school.setName("S1");
        school.addCourse("CR101");

        // Input: Add course cr101 (lower-case) to school S1
        boolean result = school.addCourse("cr101");

        // Expected Output: True
        assertTrue("Adding a course identifier with different case should return true", result);

        // Validation
        List<Course> courses = school.getCourses();
        assertEquals("Course count should be 2", 2, courses.size());
        assertEquals("First course ID mismatch", "CR101", courses.get(0).getId());
        assertEquals("Case clash course ID mismatch", "cr101", courses.get(1).getId());
    }

    @Test
    public void testAddCourseWithLongAlphanumericIdentifier() {
        // Test Case 5: "Long alphanumeric identifier"
        // Setup
        School school = new School();
        school.setName("S1");
        school.addCourse("CR101");
        school.addCourse("CR102");

        // Input: Add course CR-ADV-2025 to school S1
        boolean result = school.addCourse("CR-ADV-2025");

        // Expected Output: True
        assertTrue("Adding a long alphanumeric course identifier should return true", result);

        // Validation
        List<Course> courses = school.getCourses();
        assertEquals("Course count should be 3", 3, courses.size());
        assertEquals("Long identifier course ID mismatch", "CR-ADV-2025", courses.get(2).getId());
    }
}

/*
compile_result:



run_result:
JUnit version 4.13.2
.....
Time: 0.009

OK (5 tests)


*/