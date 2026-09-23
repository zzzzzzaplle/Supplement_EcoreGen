import static org.junit.Assert.*;
import java.time.LocalDate;
// Other required imports

public class CR5_RegisterParticipantTest {    
    // Do not generate @Before public void setUp() {...}

    @org.junit.Test
    public void testFirstEnrolmentSucceeds() {
        // Setup for test case 1
        School school = new School();
        school.setName("S1");
        school.addCourse("CO501");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 8, 5));
        
        Participant participant = new Participant();
        participant.setId("PA501");
        Session session = course.getSessions().get(0);
        
        // Test logic
        boolean result = session.registerParticipant(participant, LocalDate.of(2025, 8, 5));
        
        // Assertions
        assertEquals("Expected first enrolment to succeed", true, result);
        assertEquals("Expected session to have one participant", 1, session.getParticipants().size());
    }

    @org.junit.Test
    public void testDuplicateEnrolmentBlocked() {
        // Setup for test case 2
        School school = new School();
        school.setName("S1");
        school.addCourse("CO502");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 9, 1));
        
        Participant participant = new Participant();
        participant.setId("PA501");
        Session session = course.getSessions().get(0);
        session.registerParticipant(participant, LocalDate.of(2025, 9, 1));
        
        // Test logic
        boolean result = session.registerParticipant(participant, LocalDate.of(2025, 9, 1));
        
        // Assertions
        assertEquals("Expected duplicate enrolment to be blocked", false, result);
        assertEquals("Expected session to still have one participant", 1, session.getParticipants().size());
    }

    @org.junit.Test
    public void testSecondParticipantJoins() {
        // Setup for test case 3
        School school = new School();
        school.setName("S1");
        school.addCourse("CO502");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 9, 1));
        
        Participant participant1 = new Participant();
        participant1.setId("PA501");
        Participant participant2 = new Participant();
        participant2.setId("PA502");
        Session session = course.getSessions().get(0);
        session.registerParticipant(participant1, LocalDate.of(2025, 9, 1));
        
        // Test logic
        boolean result = session.registerParticipant(participant2, LocalDate.of(2025, 9, 1));
        
        // Assertions
        assertEquals("Expected second participant enrolment to succeed", true, result);
        assertEquals("Expected session to have two participants", 2, session.getParticipants().size());
    }

    @org.junit.Test
    public void testSameParticipantDifferentDate() {
        // Setup for test case 4
        School school = new School();
        school.setName("S1");
        school.addCourse("CO503");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 10, 3));
        course.addSession(LocalDate.of(2025, 10, 10));
        
        Participant participant = new Participant();
        participant.setId("PA503");
        Session session1 = course.getSessions().get(0);
        session1.registerParticipant(participant, LocalDate.of(2025, 10, 3));
        
        // Test logic
        Session session2 = course.getSessions().get(1);
        boolean result = session2.registerParticipant(participant, LocalDate.of(2025, 10, 10));
        
        // Assertions
        assertEquals("Expected enrolment in different session to succeed", true, result);
        assertEquals("Expected first session to have one participant", 1, session1.getParticipants().size());
        assertEquals("Expected second session to have one participant", 1, session2.getParticipants().size());
    }

    @org.junit.Test
    public void testSameParticipantDifferentCourseSameDay() {
        // Setup for test case 5
        School school = new School();
        school.setName("S1");
        school.addCourse("CO504");
        school.addCourse("CO505");

        Course course1 = school.getCourses().get(0);
        course1.addSession(LocalDate.of(2025, 11, 5));
        Session session1 = course1.getSessions().get(0);
        Participant participant = new Participant();
        participant.setId("PA504");
        session1.registerParticipant(participant, LocalDate.of(2025, 11, 5));

        Course course2 = school.getCourses().get(1);
        course2.addSession(LocalDate.of(2025, 11, 5));
        Session session2 = course2.getSessions().get(0);
        
        // Test logic
        boolean result = session2.registerParticipant(participant, LocalDate.of(2025, 11, 5));
        
        // Assertions
        assertEquals("Expected enrolment in different course session to succeed", true, result);
        assertEquals("Expected first course session to have one participant", 1, session1.getParticipants().size());
        assertEquals("Expected second course session to have one participant", 1, session2.getParticipants().size());
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