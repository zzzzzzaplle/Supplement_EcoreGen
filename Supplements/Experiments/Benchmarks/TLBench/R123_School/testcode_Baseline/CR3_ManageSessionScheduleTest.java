import static org.junit.Assert.*;

import java.lang.annotation.Documented;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class CR3_ManageSessionScheduleTest {

    @Test
    public void testAddFirstSession() {
        // Test Case 1: "Add first session"
        School school = new School();
        school.setName("S1");
        school.addCourse("CO301");
        Course course = school.getCourses().get(0);
        Document doc = new Document();
        doc.setName("D01");
        course.addDocument(doc);

        List<Session> sessions = course.addSession(LocalDate.of(2025, 2, 6));

        assertEquals("Expected one session", 1, sessions.size());
        assertEquals("Expected session date mismatch", LocalDate.of(2025, 2, 6), sessions.get(0).getDate());
    }

    @Test
    public void testAddDuplicateSessionDate() {
        // Test Case 2: "Add duplicate session date"
        School school = new School();
        school.setName("S1");
        school.addCourse("CO302");
        Course course = school.getCourses().get(0);

        course.addSession(LocalDate.of(2025, 2, 6));
        List<Session> sessions = course.addSession(LocalDate.of(2025, 2, 6));

        assertEquals("Expected one session", 1, sessions.size());
        assertEquals("Expected one session after adding duplicate date", 1,
                course.addSession(LocalDate.of(2025, 2, 6)).size());
    }

    @Test
    public void testCancelVacantSession() {
        // Test Case 3: "Cancel vacant session"
        School school = new School();
        school.setName("S1");
        school.addCourse("CO303");
        Course course = school.getCourses().get(0);

        course.addSession(LocalDate.of(2025, 3, 10));
        boolean result = course.cancelSession(LocalDate.of(2025, 3, 10));

        assertTrue("Expected true when canceling a vacant session", result);
    }

    @Test
    public void testCancelSessionWithParticipants() {
        // Test Case 4: "Cancel session that has participants"
        School school = new School();
        school.setName("S1");
        school.addCourse("CO304");
        Course course = school.getCourses().get(0);

        course.addSession(LocalDate.of(2025, 4, 1));
        Session session = course.getSessions().get(0);
        Participant participant = new Participant();
        participant.setId("P001");
        session.registerParticipant(participant, LocalDate.of(2024, 10, 20));

        boolean result = course.cancelSession(LocalDate.of(2025, 4, 1));

        assertFalse("Expected false when canceling a session with participants", result);
    }

    @Test
    public void testAddSecondSessionDifferentDay() {
        // Test Case 5: "Add second session different day"
        School school = new School();
        school.setName("S1");
        school.addCourse("CO305");
        Course course = school.getCourses().get(0);

        course.addSession(LocalDate.of(2025, 5, 10));
        List<Session> sessions = course.addSession(LocalDate.of(2025, 5, 15));

        assertEquals("Expected two sessions", 2, sessions.size());
        assertEquals("Expected session date mismatch for first session", LocalDate.of(2025, 5, 10),
                sessions.get(0).getDate());
        assertEquals("Expected session date mismatch for second session", LocalDate.of(2025, 5, 15),
                sessions.get(1).getDate());
    }
}

/*
compile_result:



run_result:
JUnit version 4.13.2
.....
Time: 0.014

OK (5 tests)


*/