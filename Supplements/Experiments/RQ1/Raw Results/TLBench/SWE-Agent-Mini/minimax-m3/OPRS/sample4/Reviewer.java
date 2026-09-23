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
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        if (this.assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment ra : this.assignments) {
            if (ra.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (this.assignments == null || this.assignments.isEmpty()) {
            return 0.0;
        }
        int submitted = 0;
        int sum = 0;
        for (ReviewAssignment ra : this.assignments) {
            if (ra.getGrade() == Grade.ACCEPT) {
                sum += 1;
                submitted++;
            } else if (ra.getGrade() == Grade.REJECT) {
                sum += 0;
                submitted++;
            }
        }
        if (submitted == 0) {
            return 0.0;
        }
        return (double) sum / submitted;
    }
}
