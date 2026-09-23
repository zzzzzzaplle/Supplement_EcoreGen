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
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        long total = 0;
        long submitted = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.ACCEPT) {
                total += 1;
                submitted++;
            } else if (assignment.getGrade() == Grade.REJECT) {
                total += 0;
                submitted++;
            }
        }
        if (submitted == 0) {
            return 0.0;
        }
        return (double) total / submitted;
    }
}
