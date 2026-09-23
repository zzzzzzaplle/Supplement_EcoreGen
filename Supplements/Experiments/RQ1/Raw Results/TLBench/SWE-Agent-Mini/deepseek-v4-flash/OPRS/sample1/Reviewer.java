import java.util.ArrayList;
import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int unsubmittedCount = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                unsubmittedCount++;
            }
        }
        return unsubmittedCount;
    }

    public double calculateSubmittedReviewAverageScore() {
        int totalSubmitted = 0;
        int totalScore = 0;

        for (ReviewAssignment assignment : assignments) {
            Grade g = assignment.getGrade();
            if (g == Grade.ACCEPT) {
                totalScore += 1;
                totalSubmitted++;
            } else if (g == Grade.REJECT) {
                totalScore += 0;
                totalSubmitted++;
            }
            // UNDECIDED is skipped
        }

        if (totalSubmitted == 0) {
            return 0.0;
        }

        return (double) totalScore / totalSubmitted;
    }
}
