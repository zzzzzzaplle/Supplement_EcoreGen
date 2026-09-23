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
        if (this.assignments == null) {
            this.assignments = new ArrayList<ReviewAssignment>();
        }
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        if (assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment != null && assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment != null) {
                Grade grade = assignment.getGrade();
                if (grade == Grade.ACCEPT) {
                    total += 1;
                    count++;
                } else if (grade == Grade.REJECT) {
                    total += 0;
                    count++;
                }
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return (double) total / count;
    }
}
