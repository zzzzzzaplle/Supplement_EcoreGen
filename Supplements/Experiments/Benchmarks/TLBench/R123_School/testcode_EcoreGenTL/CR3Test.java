package edu.trainingSchool.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.trainingSchool.TrainingSchoolFactory;
import edu.trainingSchool.School;
import edu.trainingSchool.Course;
import edu.trainingSchool.Session;
import edu.trainingSchool.Participant;
import edu.trainingSchool.Document;

import org.eclipse.emf.common.util.EList;
import java.time.LocalDate;

public class CR3Test {

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

    private Document createDocument(String name) {
        Document doc = factory.createDocument();
        doc.setName(name);
        return doc;
    }

    private Participant createParticipant(String id) {
        Participant p = factory.createParticipant();
        p.setId(id);
        return p;
    }

    // ---- CR3: Manage the session schedule of a course ----

    /**
     * Test Case 1: "Add first session"
     * Setup:
     *   1. Create school S21.
     *   2. Add course CO301. Add a document D01 to CO301.
     * Input: Add session 2025-02-06 to course CO301 in school S21.
     * Expected Output: 1 (session list size)
     */
    @Test
    public void testCase1_AddFirstSession() {
        // Setup
        School school = createSchool("S21");
        Course course = addCourseToSchool(school, "CO301");
        Document doc = createDocument("D01");
        course.addDocument(doc);

        // Action: add session on 2025-02-06
        LocalDate sessionDate = LocalDate.of(2025, 2, 6);
        EList<Session> sessions = course.addSession(sessionDate);

        // Expected: session list size = 1
        assertEquals("Session list should have 1 session", 1, sessions.size());
        assertEquals("Session date should be 2025-02-06", sessionDate, sessions.get(0).getDate());
    }

    /**
     * Test Case 2: "Add duplicate session date"
     * Setup:
     *   1. Create school S22.
     *   2. Add course CO302.
     *   3. Add one session dated 2025-02-06 to CO302.
     * Input: Add session 2025-02-06 to course CO302 again.
     * Expected Output: 1 (session list size remains 1, duplicate not added)
     */
    @Test
    public void testCase2_AddDuplicateSessionDate() {
        // Setup
        School school = createSchool("S22");
        Course course = addCourseToSchool(school, "CO302");
        LocalDate sessionDate = LocalDate.of(2025, 2, 6);
        course.addSession(sessionDate);

        // Action: add session with same date again
        EList<Session> sessions = course.addSession(sessionDate);

        // Expected: session list size remains 1
        assertEquals("Session list should still have 1 session (duplicate not added)", 1, sessions.size());
    }

    /**
     * Test Case 3: "Cancel vacant session"
     * Setup:
     *   1. Create school S23.
     *   2. Add course CO303.
     *   3. Add session 2025-03-10 to CO303 (no participants).
     * Input: Cancel session 2025-03-10 from course CO303 in school S23.
     * Expected Output: True
     */
    @Test
    public void testCase3_CancelVacantSession() {
        // Setup
        School school = createSchool("S23");
        Course course = addCourseToSchool(school, "CO303");
        LocalDate sessionDate = LocalDate.of(2025, 3, 10);
        course.addSession(sessionDate);
        assertEquals("Should have 1 session before cancel", 1, course.getSessions().size());

        // Action: cancel session on 2025-03-10
        boolean result = course.cancelSession(sessionDate);

        // Expected: True (session cancelled successfully)
        assertTrue("Should successfully cancel vacant session", result);
        assertEquals("Session list should be empty after cancel", 0, course.getSessions().size());
    }

    /**
     * Test Case 4: "Cancel session that has participants"
     * Setup:
     *   1. Create school S24.
     *   2. Add course CO304.
     *   3. Add session 2025-04-01.
     *   4. Register one participant "P001" for that session.
     * Input: Cancel session 2025-04-01 from course CO304 in school S24.
     * Expected Output: False
     */
    @Test
    public void testCase4_CancelSessionWithParticipants() {
        // Setup
        School school = createSchool("S24");
        Course course = addCourseToSchool(school, "CO304");
        LocalDate sessionDate = LocalDate.of(2025, 4, 1);
        course.addSession(sessionDate);

        Session session = course.getSessions().get(0);
        Participant p = createParticipant("P001");
        // Register participant (today must be before session date)
        LocalDate today = LocalDate.of(2025, 3, 15);
        session.registerParticipant(p, today);
        assertEquals("Session should have 1 participant", 1, session.getRegisteredParticipants().size());

        // Action: cancel session with participants
        boolean result = course.cancelSession(sessionDate);

        // Expected: False (cannot cancel session with participants)
        assertFalse("Should not cancel session that has participants", result);
        assertEquals("Session list should still have 1 session", 1, course.getSessions().size());
    }

    /**
     * Test Case 5: "Add second session different day"
     * Setup:
     *   1. Create school S25.
     *   2. Add course CO305.
     *   3. Add session 2025-05-10 to CO305.
     * Input: Add session 2025-05-15 to course CO305 in school S25.
     * Expected Output: 2 (session list now contains 2 sessions)
     */
    @Test
    public void testCase5_AddSecondSessionDifferentDay() {
        // Setup
        School school = createSchool("S25");
        Course course = addCourseToSchool(school, "CO305");
        LocalDate firstDate = LocalDate.of(2025, 5, 10);
        course.addSession(firstDate);
        assertEquals("Should have 1 session initially", 1, course.getSessions().size());

        // Action: add session on 2025-05-15
        LocalDate secondDate = LocalDate.of(2025, 5, 15);
        EList<Session> sessions = course.addSession(secondDate);

        // Expected: session list size = 2
        assertEquals("Session list should have 2 sessions", 2, sessions.size());
    }
}
