import java.util.*;

public class TestSystem {
    public static void main(String[] args) {
        // Test Requirement 1: Calculate unsubmitted reviews
        Reviewer reviewer = new Reviewer();
        ReviewAssignment r1 = new ReviewAssignment();
        r1.setGrade(Grade.UNDECIDED);
        ReviewAssignment r2 = new ReviewAssignment();
        r2.setGrade(Grade.ACCEPT);
        reviewer.addReviewAssignment(r1);
        reviewer.addReviewAssignment(r2);
        if (reviewer.calculateUnsubmittedReviews() != 1) throw new RuntimeException("Test 1 failed");

        // Test Requirement 2: Paper consistency
        Paper paper = new Paper();
        ReviewAssignment a1 = new ReviewAssignment(); a1.setGrade(Grade.ACCEPT);
        ReviewAssignment a2 = new ReviewAssignment(); a2.setGrade(Grade.ACCEPT);
        ReviewAssignment a3 = new ReviewAssignment(); a3.setGrade(Grade.ACCEPT);
        paper.addReview(a1);
        paper.addReview(a2);
        paper.addReview(a3);
        if (!paper.isAllReviewsConsistent()) throw new RuntimeException("Test 2 failed");

        // Test Requirement 3 & 4: Author submission count and acceptance rate
        Author author = new Author();
        Paper p1 = new Paper(); p1.setDecision(Grade.ACCEPT);
        Paper p2 = new Paper(); p2.setDecision(Grade.REJECT);
        author.submitPaper(p1);
        author.submitPaper(p2);
        if (author.countSubmittedPapers() != 2) throw new RuntimeException("Test 3 failed");
        if (Math.abs(author.calculateAcceptanceRate() - 0.5) > 0.001) throw new RuntimeException("Test 4 failed");

        // Test Requirement 5: Average score
        Reviewer reviewer2 = new Reviewer();
        ReviewAssignment av1 = new ReviewAssignment(); av1.setGrade(Grade.ACCEPT);
        ReviewAssignment av2 = new ReviewAssignment(); av2.setGrade(Grade.ACCEPT);
        ReviewAssignment av3 = new ReviewAssignment(); av3.setGrade(Grade.ACCEPT);
        ReviewAssignment av4 = new ReviewAssignment(); av4.setGrade(Grade.REJECT);
        reviewer2.addReviewAssignment(av1);
        reviewer2.addReviewAssignment(av2);
        reviewer2.addReviewAssignment(av3);
        reviewer2.addReviewAssignment(av4);
        if (Math.abs(reviewer2.calculateSubmittedReviewAverageScore() - 0.75) > 0.001) throw new RuntimeException("Test 5 failed");

        System.out.println("All tests passed!");
    }
}
