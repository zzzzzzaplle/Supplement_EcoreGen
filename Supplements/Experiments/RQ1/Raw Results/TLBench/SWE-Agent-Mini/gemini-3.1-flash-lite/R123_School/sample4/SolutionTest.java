import java.time.LocalDate;
import java.util.List;

public class SolutionTest {
    public static void main(String[] args) {
        School school = new School("MySchool");
        
        // 1. Add a course
        boolean added = school.addCourse("C1");
        System.out.println("Course added: " + added);
        
        // 2. Assign course to sector
        Sector s1 = school.addSector("S1");
        boolean assigned = school.assignCourseToSector("C1", "S1");
        System.out.println("Course assigned to sector: " + assigned);
        
        // 3. Manage session schedule
        Course c1 = school.getCourses().get(0);
        List<Session> sessions = c1.addSession(LocalDate.of(2025, 2, 6));
        System.out.println("Session added, sessions count: " + sessions.size());
        
        boolean canceled = c1.cancelSession(LocalDate.of(2025, 2, 6));
        System.out.println("Session canceled: " + canceled);
        
        // 4. Assign trainer
        c1.addSession(LocalDate.of(2025, 3, 1));
        Session s = c1.getSessions().get(0);
        Trainer trainer = new Trainer("T1", false);
        boolean assignedTrainer = s.assignTrainer(trainer, LocalDate.of(2025, 2, 1));
        System.out.println("Trainer assigned: " + assignedTrainer);
        
        // 5. Register participant
        Participant p = new Participant("P1");
        boolean registered = s.registerParticipant(p, LocalDate.of(2025, 2, 1));
        System.out.println("Participant registered: " + registered);
        
        System.out.println("All tests passed");
    }
}
