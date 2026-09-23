import java.util.ArrayList;
import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<ReviewAssignment>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        if (this.assignments == null) {
            this.assignments = new ArrayList<ReviewAssignment>();
        }
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        if (this.assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment ra : this.assignments) {
            if (ra != null && ra.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (this.assignments == null || this.assignments.isEmpty()) {
            return 0.0;
        }
        int totalScore = 0;
        int submittedCount = 0;
        for (ReviewAssignment ra : this.assignments) {
            if (ra == null) {
                continue;
            }
            Grade g = ra.getGrade();
            if (g == Grade.UNDECIDED) {
                continue;
            }
            submittedCount++;
            if (g == Grade.ACCEPT) {
                totalScore += 1;
            } else if (g == Grade.REJECT) {
                totalScore += 0;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / (double) submittedCount;
    }
}
