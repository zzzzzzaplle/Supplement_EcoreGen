
import static org.junit.Assert.*;
import java.util.ArrayList;

public class CR5_ReviewerTest {
    public Paper createPaper(String id, PaperType type) {
        Paper paper = new Paper();
        paper.setTitle(id);
        paper.setType(type);
        return paper;
    }

    @org.junit.Test
    public void tc1_AllAcceptances() {
        // Test logic
        User reviewerR006 = new User();
        reviewerR006.setName("R006");
        Reviewer reviewer = new Reviewer();
        reviewerR006.addRole(reviewer);

        for (int i = 0; i < 5; i++) {
            ReviewAssignment assignment = new ReviewAssignment();
            assignment.setGrade(Grade.ACCEPT); // Assign ACCEPT grades
            reviewer.addReviewAssignment(assignment);
        }
        // Assertions
        assertEquals("Expected acceptance proportion mismatch", 1.00, reviewer.calculateSubmittedReviewAverageScore(),
                0.001);
    }

    @org.junit.Test
    public void tc2_BalancedRatio() {
        // Test logic
        User reviewerR007 = new User();
        reviewerR007.setName("R007");
        Reviewer reviewer = new Reviewer();
        reviewerR007.addRole(reviewer);

        for (int i = 0; i < 3; i++) {
            ReviewAssignment assignment = new ReviewAssignment();
            assignment.setGrade(Grade.ACCEPT); // Assign ACCEPT grades
            reviewer.addReviewAssignment(assignment);
        }
        for (int i = 0; i < 3; i++) {
            ReviewAssignment assignment = new ReviewAssignment();
            assignment.setGrade(Grade.REJECT); // Assign REJECT grades
            reviewer.addReviewAssignment(assignment);
        }
        // Assertions
        assertEquals("Expected acceptance proportion mismatch", 0.50, reviewer.calculateSubmittedReviewAverageScore(),
                0.001);
    }

    @org.junit.Test
    public void tc3_NoCompletedReviews() {
        // Test logic
        User reviewerR008 = new User();
        reviewerR008.setName("R008");
        Reviewer reviewer = new Reviewer();
        reviewerR008.addRole(reviewer);

        // Assertions
        assertEquals("Expected acceptance proportion mismatch", 0.00, reviewer.calculateSubmittedReviewAverageScore(),
                0.001);
    }

    @org.junit.Test
    public void tc4_RecentRejectTendency() {
        // Test logic
        User reviewerR009 = new User();
        reviewerR009.setName("R009");
        Reviewer reviewer = new Reviewer();
        reviewerR009.addRole(reviewer);

        ReviewAssignment assignment1 = new ReviewAssignment();
        assignment1.setGrade(Grade.ACCEPT); // Assign 1 ACCEPT grade
        reviewer.addReviewAssignment(assignment1);

        for (int i = 0; i < 4; i++) {
            ReviewAssignment assignment = new ReviewAssignment();
            assignment.setGrade(Grade.REJECT); // Assign 4 REJECT grades
            reviewer.addReviewAssignment(assignment);
        }
        // Assertions
        assertEquals("Expected acceptance proportion mismatch", 0.20, reviewer.calculateSubmittedReviewAverageScore(),
                0.001);
    }

    @org.junit.Test
    public void tc5_SingleReviewCase() {
        // Test logic
        User reviewerR010 = new User();
        reviewerR010.setName("R010");
        Reviewer reviewer = new Reviewer();
        reviewerR010.addRole(reviewer);

        ReviewAssignment assignment = new ReviewAssignment();
        assignment.setGrade(Grade.REJECT); // Assign 1 REJECT grade
        reviewer.addReviewAssignment(assignment);
        // Assertions
        assertEquals("Expected acceptance proportion mismatch", 0.00, reviewer.calculateSubmittedReviewAverageScore(),
                0.001);
    }
    ReviewAssignment createAssignment(Grade grade) {
        ReviewAssignment assignment = new ReviewAssignment();
        assignment.setGrade(grade);
        return assignment;
    }
    
    @org.junit.Test
    public void tc6_MixedSubmittedAndPendingReviews() {
        // Test Case 6: Mixed submitted and pending reviews
        Reviewer reviewer = new Reviewer();
 
        reviewer.addReviewAssignment(createAssignment(Grade.ACCEPT));
        reviewer.addReviewAssignment(createAssignment(Grade.ACCEPT));

        reviewer.addReviewAssignment(createAssignment(Grade.REJECT));

        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));

        double score = reviewer.calculateSubmittedReviewAverageScore();

        assertEquals("Average score should consider only submitted reviews (2A,1R → 0.67)",
                0.67, score, 0.001);
    }

    @org.junit.Test
    public void tc7_AllReviewsPendingOnlyUndecided() {
        // Test Case 7: All reviews pending (only UNDECIDED)
        Reviewer reviewer = new Reviewer();
        
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));
        reviewer.addReviewAssignment(createAssignment(Grade.UNDECIDED));

        double score = reviewer.calculateSubmittedReviewAverageScore();



        assertEquals("Average score should be 0.00 when all reviews are UNDECIDED",
                0.00, score, 0.001);
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.018
 * 
 * OK (5 tests)
 * 
 * 
 */