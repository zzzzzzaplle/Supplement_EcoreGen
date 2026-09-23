import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.conference.CoChair;
import edu.conference.ConferenceFactory;
import edu.conference.Grade;
import edu.conference.Paper;
import edu.conference.PaperType;
import edu.conference.ReviewAssignment;
import edu.conference.Reviewer;

public class CR2Test {
    private final ConferenceFactory factory = ConferenceFactory.eINSTANCE;

    @Test
    public void unanimousAcceptFromThreeReviews() {
        Paper paper = createPaper("P14");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT);

        assertTrue(paper.isAllReviewsConsistent());
    }

    @Test
    public void validNumberOfReviewersButSplitDecision() {
        Paper paper = createPaper("P15");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.REJECT);

        assertFalse(paper.isAllReviewsConsistent());
    }

    @Test
    public void allRejectWithFourReviews() {
        Paper paper = createPaper("P16");
        addReviews(paper, Grade.REJECT, Grade.REJECT, Grade.REJECT, Grade.REJECT);

        assertTrue(paper.isAllReviewsConsistent());
    }

    @Test
    public void mixedGradesWithOnePendingReview() {
        Paper paper = createPaper("P17");
        addReviews(paper, Grade.ACCEPT, Grade.REJECT, Grade.UNDECIDED);

        assertFalse(paper.isAllReviewsConsistent());
    }

    @Test
    public void exactlyHalfAcceptanceIsNotConsistent() {
        Paper paper = createPaper("P18");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.REJECT, Grade.REJECT);

        assertFalse(paper.isAllReviewsConsistent());
    }

    @Test
    public void noReviewsForPaper() {
        Paper paper = createPaper("P19");

        assertFalse(paper.isAllReviewsConsistent());
    }

    @Test
    public void makeFinalDecisionWithAtLeastThreeConsistentSubmittedReviews() {
        Paper paper = createPaper("P20");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT);
        CoChair coChair = factory.createCoChair();

        assertEquals(1, coChair.makeFinalDecision(paper, Grade.ACCEPT));
        assertEquals(Grade.ACCEPT, paper.getDecision());
    }

    @Test
    public void rejectFinalDecisionWhenFewerThanThreeReviews() {
        Paper paper = createPaper("P21");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT);
        CoChair coChair = factory.createCoChair();

        assertEquals(-1, coChair.makeFinalDecision(paper, Grade.ACCEPT));
        assertEquals(Grade.UNDECIDED, paper.getDecision());
    }

    @Test
    public void rejectFinalDecisionWhenReviewIsUndecided() {
        Paper paper = createPaper("P22");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.UNDECIDED);
        CoChair coChair = factory.createCoChair();

        assertEquals(-1, coChair.makeFinalDecision(paper, Grade.ACCEPT));
        assertEquals(Grade.UNDECIDED, paper.getDecision());
    }

    @Test
    public void rejectFinalDecisionWhenReviewsAreInconsistent() {
        Paper paper = createPaper("P23");
        addReviews(paper, Grade.ACCEPT, Grade.ACCEPT, Grade.REJECT);
        CoChair coChair = factory.createCoChair();

        assertEquals(-1, coChair.makeFinalDecision(paper, Grade.ACCEPT));
        assertEquals(Grade.UNDECIDED, paper.getDecision());
    }

    private Paper createPaper(String title) {
        Paper paper = factory.createPaper();
        paper.setTitle(title);
        paper.setType(PaperType.RESEARCH);
        paper.setDecision(Grade.UNDECIDED);
        return paper;
    }

    private void addReviews(Paper paper, Grade... grades) {
        for (Grade grade : grades) {
            Reviewer reviewer = factory.createReviewer();
            ReviewAssignment assignment = factory.createReviewAssignment();
            assignment.setGrade(grade);
            if (grade != Grade.UNDECIDED) {
                assignment.setFeedback("revise");
            }
            assignment.setPaper(paper);
            reviewer.getAssignments().add(assignment);
        }
    }
}
