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
        double total = 0.0;
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() != Grade.UNDECIDED) {
                count++;
                if (assignment.getGrade() == Grade.ACCEPT) {
                    total += 1.0;
                }
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }
}
