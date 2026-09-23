
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class CR1_ReviewAssignmentTest {
    public Paper createPaper(String id, PaperType type) {
        Paper paper = new Paper();
        paper.setTitle(id);
        paper.setType(type);
        return paper;
    }

    @org.junit.Test
    public void tc1_ReviewerWithThreePendingReviews() {
        // Test logic
        PaperType research = PaperType.RESEARCH; // Assuming PaperType is an enum
        User reviewerR001 = new User();
        reviewerR001.setName("R001");
        Reviewer aliceReviewer = new Reviewer();
        reviewerR001.addRole(aliceReviewer);

        CoChair C001 = new CoChair();
        Paper paper1 = createPaper("P1", research);
        Paper paper2 = createPaper("P2", research);
        Paper paper3 = createPaper("P3", research);

        // Assign papers with no grades/feedback
        ReviewAssignment assignment1 = new ReviewAssignment();
        ReviewAssignment assignment2 = new ReviewAssignment();
        ReviewAssignment assignment3 = new ReviewAssignment();

        aliceReviewer.addReviewAssignment(assignment1);
        aliceReviewer.addReviewAssignment(assignment2);
        aliceReviewer.addReviewAssignment(assignment3);

        // Assertions
        assertEquals("Pending reviews count mismatch", 3, aliceReviewer.calculateUnsubmittedReviews());
    }

    @org.junit.Test
    public void tc2_AllReviewsSubmitted() {
        // Test logic
        User reviewerR002 = new User();
        reviewerR002.setName("R002");
        Reviewer reviewer = new Reviewer();
        reviewerR002.addRole(reviewer);

        Paper paper4 = createPaper("P4", PaperType.EXPERIENCE);
        Paper paper5 = createPaper("P5", PaperType.EXPERIENCE);

        // Assign papers with grades=ACCEPT
        ReviewAssignment assignment4 = new ReviewAssignment();
        Grade accept = Grade.ACCEPT; // Assuming Grade is an enum
        assignment4.setGrade(accept);
        ReviewAssignment assignment5 = new ReviewAssignment();
        assignment5.setGrade(accept);

        reviewer.addReviewAssignment(assignment4);
        reviewer.addReviewAssignment(assignment5);

        // Assertions
        assertEquals("Pending reviews count mismatch", 0, reviewer.calculateUnsubmittedReviews());
    }

    @org.junit.Test
    public void tc3_MixedSubmissionStatus() {
        // Test logic
        User reviewerR003 = new User();
        reviewerR003.setName("R003");
        Reviewer reviewer = new Reviewer();
        reviewerR003.addRole(reviewer);

        Paper paper6 = createPaper("P6", PaperType.RESEARCH);
        Paper paper7 = createPaper("P7", PaperType.RESEARCH);
        Paper paper8 = createPaper("P8", PaperType.RESEARCH);
        Paper paper9 = createPaper("P9", PaperType.RESEARCH);
        Paper paper10 = createPaper("P10", PaperType.RESEARCH);

        // Assign papers
        ReviewAssignment assignment6 = new ReviewAssignment();
        ReviewAssignment assignment7 = new ReviewAssignment();
        ReviewAssignment assignment8 = new ReviewAssignment();
        assignment8.setGrade(Grade.REJECT);
        ReviewAssignment assignment9 = new ReviewAssignment();
        assignment9.setGrade(Grade.REJECT);
        ReviewAssignment assignment10 = new ReviewAssignment();
        assignment10.setGrade(Grade.REJECT);

        reviewer.addReviewAssignment(assignment6);
        reviewer.addReviewAssignment(assignment7);
        reviewer.addReviewAssignment(assignment8);
        reviewer.addReviewAssignment(assignment9);
        reviewer.addReviewAssignment(assignment10);

        // Assertions
        assertEquals("Pending reviews count mismatch", 2, reviewer.calculateUnsubmittedReviews());
    }

    @org.junit.Test
    public void tc4_NoAssignedPapers() {
        // Test logic
        User reviewerR004 = new User();
        reviewerR004.setName("R004");
        Reviewer reviewer = new Reviewer();
        reviewerR004.addRole(reviewer);

        // Assertions
        assertEquals("Pending reviews count mismatch", 0, reviewer.calculateUnsubmittedReviews());
    }

    @org.junit.Test
    public void tc5_PartiallySubmittedReviews() {
        // Test logic
        User reviewerR005 = new User();
        reviewerR005.setName("R005");
        Reviewer reviewer = new Reviewer();
        reviewerR005.addRole(reviewer);

        Paper paper11 = createPaper("P11", PaperType.RESEARCH);
        Paper paper12 = createPaper("P12", PaperType.RESEARCH);
        Paper paper13 = createPaper("P13", PaperType.RESEARCH);

        // Assign papers
        ReviewAssignment assignment11 = new ReviewAssignment();
        assignment11.setGrade(Grade.ACCEPT);
        ReviewAssignment assignment12 = new ReviewAssignment(); // No grade
        ReviewAssignment assignment13 = new ReviewAssignment();
        assignment13.setGrade(Grade.REJECT);

        reviewer.addReviewAssignment(assignment11);
        reviewer.addReviewAssignment(assignment12);
        reviewer.addReviewAssignment(assignment13);

        // Assertions
        assertEquals("Pending reviews count mismatch", 1, reviewer.calculateUnsubmittedReviews());
    }
}