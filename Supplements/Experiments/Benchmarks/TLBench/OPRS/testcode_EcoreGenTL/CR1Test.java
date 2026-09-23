import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.conference.ConferenceFactory;
import edu.conference.Grade;
import edu.conference.Paper;
import edu.conference.PaperType;
import edu.conference.ReviewAssignment;
import edu.conference.Reviewer;

public class CR1Test {
    private final ConferenceFactory factory = ConferenceFactory.eINSTANCE;

    @Test
    public void reviewerWithThreePendingReviews() {
        Reviewer reviewer = createReviewer();
        assignReview(reviewer, "P1", Grade.UNDECIDED);
        assignReview(reviewer, "P2", Grade.UNDECIDED);
        assignReview(reviewer, "P3", Grade.UNDECIDED);

        assertEquals(3, reviewer.calculateUnsubmittedReviews());
    }

    @Test
    public void allReviewsSubmitted() {
        Reviewer reviewer = createReviewer();
        assignReview(reviewer, "P4", Grade.ACCEPT);
        assignReview(reviewer, "P5", Grade.ACCEPT);

        assertEquals(0, reviewer.calculateUnsubmittedReviews());
    }

    @Test
    public void mixedSubmissionStatus() {
        Reviewer reviewer = createReviewer();
        assignReview(reviewer, "P6", Grade.UNDECIDED);
        assignReview(reviewer, "P7", Grade.UNDECIDED);
        assignReview(reviewer, "P8", Grade.REJECT);
        assignReview(reviewer, "P9", Grade.REJECT);
        assignReview(reviewer, "P10", Grade.REJECT);

        assertEquals(2, reviewer.calculateUnsubmittedReviews());
    }

    @Test
    public void noAssignedPapers() {
        Reviewer reviewer = createReviewer();

        assertEquals(0, reviewer.calculateUnsubmittedReviews());
    }

    @Test
    public void partiallySubmittedReviews() {
        Reviewer reviewer = createReviewer();
        assignReview(reviewer, "P11", Grade.ACCEPT);
        assignReview(reviewer, "P12", Grade.UNDECIDED);
        assignReview(reviewer, "P13", Grade.REJECT);

        assertEquals(1, reviewer.calculateUnsubmittedReviews());
    }

    private Reviewer createReviewer() {
        return factory.createReviewer();
    }

    private Paper createPaper(String title) {
        Paper paper = factory.createPaper();
        paper.setTitle(title);
        paper.setType(PaperType.RESEARCH);
        return paper;
    }

    private ReviewAssignment assignReview(Reviewer reviewer, String paperTitle, Grade grade) {
        ReviewAssignment assignment = factory.createReviewAssignment();
        assignment.setPaper(createPaper(paperTitle));
        assignment.setGrade(grade);
        if (grade != Grade.UNDECIDED) {
            assignment.setFeedback("submitted");
        }
        reviewer.getAssignments().add(assignment);
        return assignment;
    }
}
