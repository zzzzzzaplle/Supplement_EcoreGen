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
        long submittedCount = assignments.stream()
                .filter(a -> a.getGrade() != Grade.UNDECIDED)
                .count();
        if (submittedCount == 0) {
            return 0.0;
        }
        long acceptCount = assignments.stream()
                .filter(a -> a.getGrade() == Grade.ACCEPT)
                .count();
        return (double) acceptCount / submittedCount;
    }
}
