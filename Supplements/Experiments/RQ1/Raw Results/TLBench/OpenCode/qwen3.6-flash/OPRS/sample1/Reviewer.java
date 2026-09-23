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
        int submittedReviewCount = 0;
        int totalScore = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.ACCEPT) {
                totalScore += 1;
                submittedReviewCount++;
            } else if (assignment.getGrade() == Grade.REJECT) {
                totalScore += 0;
                submittedReviewCount++;
            }
        }
        if (submittedReviewCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedReviewCount;
    }
}
