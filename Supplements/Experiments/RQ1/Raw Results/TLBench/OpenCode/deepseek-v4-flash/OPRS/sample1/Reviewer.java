import java.util.ArrayList;
import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public int calculateUnsubmittedReviews() {
        int count = 0;
        for (ReviewAssignment ra : assignments) {
            if (ra.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        int total = 0;
        int count = 0;
        for (ReviewAssignment ra : assignments) {
            Grade g = ra.getGrade();
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

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        assignments.add(assignment);
    }
}
