
import static org.junit.Assert.*;
import java.util.ArrayList;
// Other required imports

public class CR2_PaperConsensusTest {
    public Paper createPaper(String id, PaperType type) {
        Paper paper = new Paper();
        paper.setTitle(id);
        paper.setType(type);
        return paper;
    }

    @org.junit.Test
    public void tc1_UnanimousAcceptFromThreeReviews() {
        // Create Paper P14
        Paper paperP14 = createPaper("P14", PaperType.RESEARCH);
        // Create 3 reviewers and their reviews for the paper
        ReviewAssignment review1 = new ReviewAssignment();
        review1.setGrade(Grade.ACCEPT);
        review1.setFeedback("revise");

        ReviewAssignment review2 = new ReviewAssignment();
        review2.setGrade(Grade.ACCEPT);
        review2.setFeedback("revise");

        ReviewAssignment review3 = new ReviewAssignment();
        review3.setGrade(Grade.ACCEPT);
        review3.setFeedback("revise");

        // Assign reviews to the paper
        paperP14.addReview(review1);
        paperP14.addReview(review2);
        paperP14.addReview(review3);

        // Assertions
        assertTrue("Consensus should be true for unanimous accept", paperP14.isAllReviewsConsistent());
    }

    @org.junit.Test
    public void tc2_SplitDecisionTwoOne() {
        // Create Paper P15
        Paper paperP15 = createPaper("P15", PaperType.RESEARCH);

        // Create 3 reviewers and their reviews for the paper
        ReviewAssignment review1 = new ReviewAssignment();
        review1.setGrade(Grade.ACCEPT);

        ReviewAssignment review2 = new ReviewAssignment();
        review2.setGrade(Grade.ACCEPT);

        ReviewAssignment review3 = new ReviewAssignment();
        review3.setGrade(Grade.REJECT);

        // Assign reviews to the paper
        paperP15.addReview(review1);
        paperP15.addReview(review2);
        paperP15.addReview(review3);

        // Assertions
        assertFalse("Consensus should be false for split decision", paperP15.isAllReviewsConsistent());
    }

    @org.junit.Test
    public void tc3_AllRejectWithFourReviews() {
        // Create Paper P16
        Paper paperP16 = createPaper("P16", PaperType.RESEARCH);

        // Create 4 reviewers and their reviews for the paper
        ReviewAssignment review1 = new ReviewAssignment();
        review1.setGrade(Grade.REJECT);

        ReviewAssignment review2 = new ReviewAssignment();
        review2.setGrade(Grade.REJECT);

        ReviewAssignment review3 = new ReviewAssignment();
        review3.setGrade(Grade.REJECT);

        ReviewAssignment review4 = new ReviewAssignment();
        review4.setGrade(Grade.REJECT);

        // Assign reviews to the paper
        paperP16.addReview(review1);
        paperP16.addReview(review2);
        paperP16.addReview(review3);
        paperP16.addReview(review4);

        // Assertions
        assertTrue("Consensus should be true for all reject reviews", paperP16.isAllReviewsConsistent());
    }

    @org.junit.Test
    public void tc4_MixedGradesWithThreeReviews() {
        // Create Paper P17
        Paper paperP17 = createPaper("P17", PaperType.RESEARCH);

        // Create 3 reviewers and their reviews for the paper
        ReviewAssignment review1 = new ReviewAssignment();
        review1.setGrade(Grade.ACCEPT);

        ReviewAssignment review2 = new ReviewAssignment();
        review2.setGrade(Grade.REJECT);

        ReviewAssignment review3 = new ReviewAssignment();
        review3.setGrade(Grade.UNDECIDED); // No feedback given

        // Assign reviews to the paper
        paperP17.addReview(review1);
        paperP17.addReview(review2);
        paperP17.addReview(review3);

        // Assertions
        assertFalse("Consensus should be false for mixed grades", paperP17.isAllReviewsConsistent());
    }

    @org.junit.Test
    public void tc5_EdgeCaseExactlyFiftyPercentAcceptance() {
        // Create Paper P18
        Paper paperP18 = createPaper("P18", PaperType.RESEARCH);

        // Create 4 reviewers and their reviews for the paper
        ReviewAssignment review1 = new ReviewAssignment();
        review1.setGrade(Grade.ACCEPT);

        ReviewAssignment review2 = new ReviewAssignment();
        review2.setGrade(Grade.ACCEPT);

        ReviewAssignment review3 = new ReviewAssignment();
        review3.setGrade(Grade.REJECT);

        ReviewAssignment review4 = new ReviewAssignment();
        review4.setGrade(Grade.REJECT);

        // Assign reviews to the paper
        paperP18.addReview(review1);
        paperP18.addReview(review2);
        paperP18.addReview(review3);
        paperP18.addReview(review4);

        // Assertions
        assertFalse("Consensus should be false for exactly 50% acceptance", paperP18.isAllReviewsConsistent());
    }

    @org.junit.Test
    public void tc6_NoReviewsForPaper_ConsistencyIsFalse() {
        // Test Case 6: No reviews for the paper
        Paper paper = new Paper();

        boolean consistent = paper.isAllReviewsConsistent();

        assertFalse("Paper with no reviews should not be considered consistent", consistent);
    }

    Paper createPaper() {
        return new Paper();
    }

    ReviewAssignment createReview(Grade grade) {
        ReviewAssignment review = new ReviewAssignment();
        review.setGrade(grade);
        return review;
    }

    CoChair createCoChair() {
        return new CoChair();
    }

    @org.junit.Test
    public void tc7_ValidDecisionWithAtLeastThreeConsistentSubmittedReviews() {
        // Test Case 7
        Paper paper = createPaper();

        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.ACCEPT));

        CoChair coChair = createCoChair();

        int result = coChair.makeFinalDecision(paper, Grade.ACCEPT);

        assertEquals("Expected success code 1 when all reviews are consistent and >=3",
                1, result);
        assertEquals("Paper decision should be set to ACCEPT",
                Grade.ACCEPT, paper.getDecision());
    }

    @org.junit.Test
    public void tc8_RejectDecisionWhenFewerThanThreeReviews() {
        // Test Case 8
        Paper paper = createPaper();

        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.ACCEPT));
        
        CoChair coChair = createCoChair();

        int result = coChair.makeFinalDecision(paper, Grade.ACCEPT);

        assertEquals("Should return -1 when there are fewer than 3 reviews",
                -1, result);
        assertEquals("Paper decision should remain UNDECIDED when decision is rejected",
                Grade.UNDECIDED, paper.getDecision());
    }

    @org.junit.Test
    public void tc9_RejectDecisionWhenAnyReviewIsUndecided() {
        // Test Case 9
        Paper paper = createPaper();

        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.UNDECIDED)); // 有未提交

        CoChair coChair = createCoChair();

        int result = coChair.makeFinalDecision(paper, Grade.ACCEPT);

        assertEquals("Should return -1 when at least one review is UNDECIDED",
                -1, result);
        assertEquals("Paper decision should remain UNDECIDED when there is an UNDECIDED review",
                Grade.UNDECIDED, paper.getDecision());
    }

    @org.junit.Test
    public void tc10_RejectDecisionWhenReviewsAreInconsistent() {
        // Test Case 10
        Paper paper = createPaper();

        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.ACCEPT));
        paper.addReview(createReview(Grade.REJECT)); // 不一致

        CoChair coChair = createCoChair();

        int result = coChair.makeFinalDecision(paper, Grade.ACCEPT);

        assertEquals("Should return -1 when reviews are not all ACCEPT or all REJECT",
                -1, result);
        assertEquals("Paper decision should remain UNDECIDED when reviews are inconsistent",
                Grade.UNDECIDED, paper.getDecision());
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
 * Time: 0.012
 * 
 * OK (5 tests)
 * 
 * 
 */