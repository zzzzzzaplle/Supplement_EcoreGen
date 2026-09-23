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
        int totalSubmitted = 0;
        int scoreSum = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.ACCEPT) {
                scoreSum += 1;
                totalSubmitted++;
            } else if (assignment.getGrade() == Grade.REJECT) {
                scoreSum += 0;
                totalSubmitted++;
            }
        }
        if (totalSubmitted == 0) {
            return 0.0;
        }
        return (double) scoreSum / totalSubmitted;
    }
}
