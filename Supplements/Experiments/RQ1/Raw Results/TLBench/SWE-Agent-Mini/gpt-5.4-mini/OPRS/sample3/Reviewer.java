import java.util.ArrayList;
import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<ReviewAssignment>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        if (assignments == null) {
            assignments = new ArrayList<ReviewAssignment>();
        }
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        getReviewAssignments().add(assignment);
    }

    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    public int calculateUnsubmittedReviews() {
        int count = 0;
        for (ReviewAssignment assignment : getReviewAssignments()) {
            if (assignment != null && assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        int submitted = 0;
        int score = 0;
        for (ReviewAssignment assignment : getReviewAssignments()) {
            if (assignment == null) {
                continue;
            }
            Grade grade = assignment.getGrade();
            if (grade == Grade.ACCEPT) {
                submitted++;
                score += 1;
            } else if (grade == Grade.REJECT) {
                submitted++;
            }
        }
        if (submitted == 0) {
            return 0.0;
        }
        return ((double) score) / submitted;
    }
}
