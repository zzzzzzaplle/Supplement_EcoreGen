package edu.trainingSchool.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.trainingSchool.TrainingSchoolFactory;
import edu.trainingSchool.School;
import edu.trainingSchool.Course;
import edu.trainingSchool.Session;
import edu.trainingSchool.Participant;

import java.time.LocalDate;

public class CR5Test {

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

    private Participant createParticipant(String id) {
        Participant p = factory.createParticipant();
        p.setId(id);
        return p;
    }

    // ---- CR5: Register a participant for a session ----

    /**
     * Test Case 1: "First enrolment succeeds"
     * Setup:
     *   1. Create school S41 with course CO501.
     *   2. Add a session 2025-08-05 (no participants) to course CO501.
     * Input: Register participant PA501 for session 2025-08-05 in course CO501, school S41.
     * Expected Output: True
     */
    @Test
    public void testCase1_FirstEnrolment() {
        // Setup
        School school = createSchool("S41");
        Course course = addCourseToSchool(school, "CO501");
        LocalDate sessionDate = LocalDate.of(2025, 8, 5);
        Session session = addSessionToCourse(course, sessionDate);
        Participant pa501 = createParticipant("PA501");

        // Action: register PA501 (today well before session date)
        LocalDate today = LocalDate.of(2025, 7, 1);
        boolean result = session.registerParticipant(pa501, today);

        // Expected: True
        assertTrue("First enrolment should succeed", result);
        assertEquals("Session should have 1 participant", 1, session.getRegisteredParticipants().size());
    }

    /**
     * Test Case 2: "Duplicate enrolment blocked"
     * Setup:
     *   1. Create school S42 with course CO502.
     *   2. Add a session 2025-09-01 to course CO502.
     *   3. Register PA501 once for that session.
     * Input: Register participant PA501 again for the same session.
     * Expected Output: False
     */
    @Test
    public void testCase2_DuplicateEnrolmentBlocked() {
        // Setup
        School school = createSchool("S42");
        Course course = addCourseToSchool(school, "CO502");
        LocalDate sessionDate = LocalDate.of(2025, 9, 1);
        Session session = addSessionToCourse(course, sessionDate);
        Participant pa501 = createParticipant("PA501");

        LocalDate today = LocalDate.of(2025, 7, 1);
        // First registration
        boolean firstResult = session.registerParticipant(pa501, today);
        assertTrue("First registration should succeed", firstResult);

        // Action: register PA501 again for same session
        boolean result = session.registerParticipant(pa501, today);

        // Expected: False
        assertFalse("Duplicate enrolment should be blocked", result);
        assertEquals("Session should still have 1 participant", 1, session.getRegisteredParticipants().size());
    }

    /**
     * Test Case 3: "Second participant joins"
     * Setup:
     *   1. Create school S42 with course CO502.
     *   2. Add a session 2025-09-01 (one participant PA501) to course CO502.
     * Input: Register participant PA502 for session 2025-09-01 in course CO502, school S42.
     * Expected Output: True
     */
    @Test
    public void testCase3_SecondParticipantJoins() {
        // Setup
        School school = createSchool("S42");
        Course course = addCourseToSchool(school, "CO502");
        LocalDate sessionDate = LocalDate.of(2025, 9, 1);
        Session session = addSessionToCourse(course, sessionDate);

        Participant pa501 = createParticipant("PA501");
        Participant pa502 = createParticipant("PA502");

        LocalDate today = LocalDate.of(2025, 7, 1);
        session.registerParticipant(pa501, today);
        assertEquals("Session should have 1 participant initially", 1, session.getRegisteredParticipants().size());

        // Action: register PA502
        boolean result = session.registerParticipant(pa502, today);

        // Expected: True
        assertTrue("Second participant should join successfully", result);
        assertEquals("Session should have 2 participants", 2, session.getRegisteredParticipants().size());
    }

    /**
     * Test Case 4: "Same participant different date"
     * Setup:
     *   1. Create school S43, course CO503.
     *   2. Register PA503 for session 2025-10-03.
     *   3. Create session 2025-10-10 (no participants yet).
     * Input: Register participant PA503 for session 2025-10-10 in course CO503, school S43.
     * Expected Output: True.
     */
    @Test
    public void testCase4_SameParticipantDifferentDate() {
        // Setup
        School school = createSchool("S43");
        Course course = addCourseToSchool(school, "CO503");

        LocalDate date1 = LocalDate.of(2025, 10, 3);
        LocalDate date2 = LocalDate.of(2025, 10, 10);
        Session session1 = addSessionToCourse(course, date1);
        Session session2 = addSessionToCourse(course, date2);

        Participant pa503 = createParticipant("PA503");
        LocalDate today = LocalDate.of(2025, 7, 1);

        // Register PA503 for first session
        boolean firstResult = session1.registerParticipant(pa503, today);
        assertTrue("First session registration should succeed", firstResult);
        assertEquals("Session 2025-10-03 should have 1 participant", 1, session1.getRegisteredParticipants().size());

        // Action: register PA503 for second session (different date)
        boolean result = session2.registerParticipant(pa503, today);

        // Expected: True (same participant can join different session)
        assertTrue("Same participant should register for different date session", result);
        assertEquals("Session 2025-10-10 should have 1 participant", 1, session2.getRegisteredParticipants().size());
    }

    /**
     * Test Case 5: "Same participant different course same day"
     * Setup:
     *   1. Create school S44.
     *   2. Course CO504 with session 2025-11-05 already has participant PA504.
     *   3. Add course CO505 with its own session on 2025-11-05 (no participants).
     * Input: Register participant PA504 for session 2025-11-05 in course CO505, school S44.
     * Expected Output: True
     */
    @Test
    public void testCase5_SameParticipantDifferentCourseSameDay() {
        // Setup
        School school = createSchool("S44");
        Course course504 = addCourseToSchool(school, "CO504");
        Course course505 = addCourseToSchool(school, "CO505");

        LocalDate sessionDate = LocalDate.of(2025, 11, 5);
        Session session504 = addSessionToCourse(course504, sessionDate);
        Session session505 = addSessionToCourse(course505, sessionDate);

        Participant pa504 = createParticipant("PA504");
        LocalDate today = LocalDate.of(2025, 7, 1);

        // Register PA504 for CO504's session
        boolean firstResult = session504.registerParticipant(pa504, today);
        assertTrue("CO504 registration should succeed", firstResult);
        assertEquals("CO504 session should have 1 participant", 1, session504.getRegisteredParticipants().size());

        // Action: register PA504 for CO505's session on the same day
        boolean result = session505.registerParticipant(pa504, today);

        // Expected: True (same participant can join different course on same day)
        assertTrue("Same participant should register for different course on same day", result);
        assertEquals("CO505 session should have 1 participant", 1, session505.getRegisteredParticipants().size());
    }
}
