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

    // Functional Requirement 1: Calculate unsubmitted reviews
    public int calculateUnsubmittedReviews() {
        if (assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment ra : assignments) {
            if (ra != null && ra.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    // Functional Requirement 5: Calculate average score of submitted reviews
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null) {
            return 0.0;
        }
        
        double sum = 0.0;
        int count = 0;
        
        for (ReviewAssignment ra : assignments) {
            if (ra != null && ra.getGrade() != Grade.UNDECIDED) {
                count++;
                if (ra.getGrade() == Grade.ACCEPT) {
                    sum += 1.0;
                } else if (ra.getGrade() == Grade.REJECT) {
                    sum += 0.0;
                }
            }
        }
        
        if (count == 0) {
            return 0.0;
        }
        
        return sum / count;
    }
}
