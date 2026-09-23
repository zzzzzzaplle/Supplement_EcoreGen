package edu.trainingSchool.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.trainingSchool.TrainingSchoolFactory;
import edu.trainingSchool.School;
import edu.trainingSchool.Course;
import edu.trainingSchool.Session;
import edu.trainingSchool.Trainer;

import java.time.LocalDate;

public class CR4Test {

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

    private Course addCourseToSchool(School school, String courseId) {
        school.addCourse(courseId);
        for (Course c : school.getCourses()) {
            if (c.getId().equals(courseId)) {
                return c;
            }
        }
        return null;
    }

    private Session addSessionToCourse(Course course, LocalDate date) {
        course.addSession(date);
        for (Session s : course.getSessions()) {
            if (s.getDate().equals(date)) {
                return s;
            }
        }
        return null;
    }

    private Trainer createTrainer(String id, boolean contractor) {
        Trainer trainer = factory.createTrainer();
        trainer.setId(id);
        trainer.setContractor(contractor);
        return trainer;
    }

    // ---- CR4: Assign or replace a trainer for a session ----

    /**
     * Test Case 1: "Initial trainer assignment"
     * Setup:
     *   1. Today = 2025-05-01.
     *   2. Create school S31 with course CO401 which has a session on 2025-06-20 (no trainer).
     * Input: Assign trainer TR401 to session 2025-06-20 in course CO401, school S31.
     * Expected Output: True
     */
    @Test
    public void testCase1_InitialTrainerAssignment() {
        // Setup
        LocalDate today = LocalDate.of(2025, 5, 1);
        School school = createSchool("S31");
        Course course = addCourseToSchool(school, "CO401");
        LocalDate sessionDate = LocalDate.of(2025, 6, 20);
        Session session = addSessionToCourse(course, sessionDate);
        Trainer trainer = createTrainer("TR401", false);

        // Action
        boolean result = session.assignTrainer(trainer, today);

        // Expected: True
        assertTrue("Should successfully assign trainer before session date", result);
        assertEquals("Session trainer should be TR401", trainer, session.getTrainer());
    }

    /**
     * Test Case 2: "Replace trainer while still ahead of date"
     * Setup:
     *   1. Today = 2025-05-10.
     *   2. Create school S32 with course CO402 which has a session on 2025-06-20 (no trainer).
     *   3. Assign trainer TR402 to session 2025-06-20 in course CO402, school S32.
     * Input: Replace trainer TR403 for TR402 in same session.
     * Expected Output: True
     */
    @Test
    public void testCase2_ReplaceTrainerAheadOfDate() {
        // Setup
        LocalDate today = LocalDate.of(2025, 5, 10);
        School school = createSchool("S32");
        Course course = addCourseToSchool(school, "CO402");
        LocalDate sessionDate = LocalDate.of(2025, 6, 20);
        Session session = addSessionToCourse(course, sessionDate);

        Trainer tr402 = createTrainer("TR402", false);
        Trainer tr403 = createTrainer("TR403", false);

        // First assign TR402
        boolean assigned = session.assignTrainer(tr402, today);
        assertTrue("Initial assignment should succeed", assigned);
        assertEquals("Trainer should be TR402", tr402, session.getTrainer());

        // Action: replace TR402 with TR403
        boolean result = session.replaceTrainer(tr403, today);

        // Expected: True
        assertTrue("Should successfully replace trainer while ahead of date", result);
        assertEquals("Session trainer should be TR403", tr403, session.getTrainer());
    }

    /**
     * Test Case 3: "Attempt replacement on session date"
     * Setup:
     *   1. Today = 2025-06-20 (session date).
     *   2. Create school S33 with course CO404 which has a session on 2025-06-20 (no trainer).
     *   3. Assign trainer TR404 to session 2025-06-20 in course CO404, school S33.
     * Input: Replace trainer TR405 for TR404 in same session.
     * Expected Output: False
     */
    @Test
    public void testCase3_ReplacementOnSessionDate() {
        // Setup
        School school = createSchool("S33");
        Course course = addCourseToSchool(school, "CO404");
        LocalDate sessionDate = LocalDate.of(2025, 6, 20);
        Session session = addSessionToCourse(course, sessionDate);

        Trainer tr404 = createTrainer("TR404", false);
        Trainer tr405 = createTrainer("TR405", false);

        // Assign TR404 with an earlier today
        LocalDate earlyToday = LocalDate.of(2025, 5, 1);
        boolean assigned = session.assignTrainer(tr404, earlyToday);
        assertTrue("Initial assignment should succeed", assigned);

        // Action: attempt replacement on session date (today = 2025-06-20)
        LocalDate sameDayToday = LocalDate.of(2025, 6, 20);
        boolean result = session.replaceTrainer(tr405, sameDayToday);

        // Expected: False (same-day operations not allowed)
        assertFalse("Should not allow replacement on session date", result);
        assertEquals("Trainer should still be TR404", tr404, session.getTrainer());
    }

    /**
     * Test Case 4: "Assign after session finished"
     * Setup:
     *   1. Today = 2025-07-01.
     *   2. Create school S34 with course CO405 which has a session on 2025-04-01 (no trainer).
     * Input: Assign trainer TR406 for past session 2025-04-01.
     * Expected Output: False
     */
    @Test
    public void testCase4_AssignAfterSessionFinished() {
        // Setup
        School school = createSchool("S34");
        Course course = addCourseToSchool(school, "CO405");
        LocalDate sessionDate = LocalDate.of(2025, 4, 1);
        Session session = addSessionToCourse(course, sessionDate);
        Trainer tr406 = createTrainer("TR406", false);

        // Action: assign trainer after session date (today = 2025-07-01)
        LocalDate today = LocalDate.of(2025, 7, 1);
        boolean result = session.assignTrainer(tr406, today);

        // Expected: False
        assertFalse("Should not allow assignment after session date", result);
        assertNull("Session should have no trainer", session.getTrainer());
    }

    /**
     * Test Case 5: "Assign contractor trainer"
     * Setup:
     *   1. Today = 2025-06-30.
     *   2. Create school S36, course CO407, session 2025-07-15 (no trainer).
     *   3. Trainer TR407 is marked as contractor.
     * Input: Set contractor TR407 for session 2025-07-15 in course CO407, school S36.
     * Expected Output: True. TR407 is a contractor.
     */
    @Test
    public void testCase5_AssignContractorTrainer() {
        // Setup
        LocalDate today = LocalDate.of(2025, 6, 30);
        School school = createSchool("S36");
        Course course = addCourseToSchool(school, "CO407");
        LocalDate sessionDate = LocalDate.of(2025, 7, 15);
        Session session = addSessionToCourse(course, sessionDate);
        Trainer tr407 = createTrainer("TR407", true); // contractor
        assertTrue("TR407 should be a contractor", tr407.isContractor());

        // Action
        boolean result = session.assignTrainer(tr407, today);

        // Expected: True, TR407 is a contractor
        assertTrue("Should successfully assign contractor trainer", result);
        assertEquals("Session trainer should be TR407", tr407, session.getTrainer());
        assertTrue("Assigned trainer should be a contractor", session.getTrainer().isContractor());
    }
}
