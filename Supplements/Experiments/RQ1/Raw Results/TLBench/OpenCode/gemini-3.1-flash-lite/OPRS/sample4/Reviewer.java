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
        for (ReviewAssignment a : assignments) {
            if (a.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        List<ReviewAssignment> submitted = new ArrayList<>();
        for (ReviewAssignment a : assignments) {
            if (a.getGrade() != Grade.UNDECIDED) {
                submitted.add(a);
            }
        }
        if (submitted.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (ReviewAssignment a : submitted) {
            if (a.getGrade() == Grade.ACCEPT) {
                sum += 1.0;
            }
        }
        return sum / submitted.size();
    }
}
