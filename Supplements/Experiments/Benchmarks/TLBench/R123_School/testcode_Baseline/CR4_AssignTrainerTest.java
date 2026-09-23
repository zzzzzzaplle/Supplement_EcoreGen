import static org.junit.Assert.*;
import java.time.LocalDate;
import org.junit.Test;

public class CR4_AssignTrainerTest {

    @Test
    public void testInitialTrainerAssignment() {
        // Test Case 1: Initial trainer assignment
        School school = new School();
        school.setName("S1");
        school.addCourse("CO401");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 6, 20));

        Session session = course.getSessions().get(0);
        Trainer trainer = new Trainer();
        trainer.setId("TR401");
        trainer.setContractor(false);

        LocalDate today = LocalDate.of(2025, 5, 1);
        boolean result = session.assignTrainer(trainer, today);

        assertTrue("Trainer should be assigned successfully", result);
        assertEquals("Trainer ID mismatch", "TR401", session.getTrainer().getId());
    }

    @Test
    public void testReplaceTrainerWhileStillAheadOfDate() {
        // Test Case 2: Replace trainer while still ahead of date
        School school = new School();
        school.setName("S1");
        school.addCourse("CO402");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 6, 20));

        Session session = course.getSessions().get(0);
        Trainer oldTrainer = new Trainer();
        oldTrainer.setId("TR402");
        oldTrainer.setContractor(false);
        Trainer newTrainer = new Trainer();
        newTrainer.setId("TR403");
        newTrainer.setContractor(false);

        LocalDate assignDate = LocalDate.of(2025, 5, 10);
        boolean initialAssign = session.replaceTrainer(oldTrainer, assignDate);
        boolean result = session.replaceTrainer(newTrainer, assignDate);

        assertTrue("Initial trainer assignment should succeed", initialAssign);
        assertTrue("Replacing trainer should succeed", result);
        assertEquals("Trainer ID mismatch after replacement", "TR403", session.getTrainer().getId());
    }

    @Test
    public void testAttemptReplacementOnSessionDate() {
        // Test Case 3: Attempt replacement on session date
        School school = new School();
        school.setName("S1");
        school.addCourse("CO404");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 6, 20));

        Session session = course.getSessions().get(0);
        Trainer initialTrainer = new Trainer();
        initialTrainer.setId("TR404");
        initialTrainer.setContractor(false);

        // First assign the trainer before session date
        LocalDate beforeDate = LocalDate.of(2025, 5, 1);
        session.replaceTrainer(initialTrainer, beforeDate);

        // Now try to replace on session date
        Trainer newTrainer = new Trainer();
        newTrainer.setId("TR405");
        newTrainer.setContractor(false);
        LocalDate sessionDate = LocalDate.of(2025, 6, 20);
        boolean result = session.replaceTrainer(newTrainer, sessionDate);

        assertFalse("Trainer should not be replaced on the same date as session", result);
        assertEquals("Trainer should remain unchanged", "TR404", session.getTrainer().getId());
    }

    @Test
    public void testAssignAfterSessionFinished() {
        // Test Case 4: Assign after session finished
        School school = new School();
        school.setName("S1");
        school.addCourse("CO405");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 4, 1));

        Session session = course.getSessions().get(0);
        Trainer trainer = new Trainer();
        trainer.setId("TR406");
        trainer.setContractor(false);

        LocalDate today = LocalDate.of(2025, 7, 1);
        boolean result = session.assignTrainer(trainer, today);

        assertFalse("Trainer should not be assigned after session finished", result);
    }

    @Test
    public void testAssignContractorTrainer() {
        // Test Case 5: Assign contractor trainer
        School school = new School();
        school.setName("S1");
        school.addCourse("CO407");
        Course course = school.getCourses().get(0);
        course.addSession(LocalDate.of(2025, 7, 15));

        Session session = course.getSessions().get(0);
        Trainer contractorTrainer = new Trainer();
        contractorTrainer.setId("TR407");
        contractorTrainer.setContractor(true);

        LocalDate today = LocalDate.of(2025, 6, 30);
        boolean result = session.assignTrainer(contractorTrainer, today);

        assertTrue("Contractor trainer should be assigned successfully", result);
        assertEquals("Trainer ID mismatch", "TR407", session.getTrainer().getId());
        assertTrue("Trainer should be a contractor", session.getTrainer().isContractor());
    }
}

/*
compile_result:



run_result:
JUnit version 4.13.2
.....
Time: 0.011

OK (5 tests)


*/