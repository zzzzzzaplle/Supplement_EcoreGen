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
        if (assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment r : assignments) {
            if (r.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null) {
            return 0.0;
        }
        int total = 0;
        int count = 0;
        for (ReviewAssignment r : assignments) {
            Grade g = r.getGrade();
            if (g == Grade.ACCEPT) {
                total += 1;
                count++;
            } else if (g == Grade.REJECT) {
                total += 0;
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return (double) total / count;
    }
}
