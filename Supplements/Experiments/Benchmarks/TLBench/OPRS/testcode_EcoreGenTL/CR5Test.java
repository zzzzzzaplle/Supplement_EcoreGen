import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.conference.ConferenceFactory;
import edu.conference.Grade;
import edu.conference.Paper;
import edu.conference.PaperType;
import edu.conference.ReviewAssignment;
import edu.conference.Reviewer;

public class CR5Test {
    private static final double DELTA = 0.001;
    private final ConferenceFactory factory = ConferenceFactory.eINSTANCE;

    @Test
    public void allAcceptances() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT);

        assertEquals(1.00, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    @Test
    public void balancedFiftyFiftyRatio() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.ACCEPT, Grade.ACCEPT, Grade.ACCEPT,
                Grade.REJECT, Grade.REJECT, Grade.REJECT);

        assertEquals(0.50, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    @Test
    public void noCompletedReviews() {
        Reviewer reviewer = createReviewer();

        assertEquals(0.00, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    @Test
    public void recentRejectTendency() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.ACCEPT, Grade.REJECT, Grade.REJECT, Grade.REJECT, Grade.REJECT);

        assertEquals(0.20, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    @Test
    public void singleReviewCase() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.REJECT);

        assertEquals(0.00, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    @Test
    public void mixedSubmittedAndPendingReviews() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.ACCEPT, Grade.ACCEPT, Grade.REJECT,
                Grade.UNDECIDED, Grade.UNDECIDED, Grade.UNDECIDED);

        assertEquals(0.67, reviewer.calculateSubmittedReviewAverageScore(), 0.01);
    }

    @Test
    public void allReviewsPending() {
        Reviewer reviewer = createReviewer();
        assignReviews(reviewer, Grade.UNDECIDED, Grade.UNDECIDED, Grade.UNDECIDED, Grade.UNDECIDED);

        assertEquals(0.00, reviewer.calculateSubmittedReviewAverageScore(), DELTA);
    }

    private Reviewer createReviewer() {
        return factory.createReviewer();
    }

    private void assignReviews(Reviewer reviewer, Grade... grades) {
        int index = 1;
        for (Grade grade : grades) {
            ReviewAssignment assignment = factory.createReviewAssignment();
            assignment.setPaper(createPaper("P" + index++));
            assignment.setGrade(grade);
            if (grade != Grade.UNDECIDED) {
                assignment.setFeedback("submitted");
            }
            reviewer.getAssignments().add(assignment);
        }
    }

    private Paper createPaper(String title) {
        Paper paper = factory.createPaper();
        paper.setTitle(title);
        paper.setType(PaperType.RESEARCH);
        return paper;
    }
}
