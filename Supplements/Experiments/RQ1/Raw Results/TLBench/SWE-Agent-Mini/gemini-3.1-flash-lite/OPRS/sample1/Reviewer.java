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

    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
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
        List<Grade> submitted = new ArrayList<>();
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() != Grade.UNDECIDED) {
                submitted.add(assignment.getGrade());
            }
        }
        if (submitted.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Grade g : submitted) {
            if (g == Grade.ACCEPT) {
                sum += 1.0;
            }
        }
        return sum / submitted.size();
    }
}
