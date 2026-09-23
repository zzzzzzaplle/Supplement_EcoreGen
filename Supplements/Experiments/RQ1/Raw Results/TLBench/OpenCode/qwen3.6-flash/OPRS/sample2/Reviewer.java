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
        assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        int totalScore = 0;
        int submittedCount = 0;
        for (ReviewAssignment assignment : assignments) {
            Grade grade = assignment.getGrade();
            if (grade == Grade.ACCEPT) {
                totalScore += 1;
                submittedCount++;
            } else if (grade == Grade.REJECT) {
                totalScore += 0;
                submittedCount++;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedCount;
    }
}
