import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void setAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        if (this.assignments == null) {
            this.assignments = new java.util.ArrayList<>();
        }
        this.assignments.add(assignment);
    }

    /**
     * Calculate the number of unsubmitted reviews for a given reviewer.
     * Return 0 if all reviews are submitted by the reviewer.
     */
    public int calculateUnsubmittedReviews() {
        if (assignments == null) {
            return 0;
        }
        
        int unsubmittedCount = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                unsubmittedCount++;
            }
        }
        return unsubmittedCount;
    }

    /**
     * Convert a reviewer's submitted review grades into a numerical average score
     * between 0.0 and 1.0, considering only submitted reviews (excluding UNDECIDED)
     * and treating ACCEPT as 1 and REJECT as 0.
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null) {
            return 0.0;
        }
        
        double sum = 0.0;
        int count = 0;
        
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.ACCEPT) {
                sum += 1.0;
                count++;
            } else if (assignment.getGrade() == Grade.REJECT) {
                sum += 0.0;
                count++;
            }
            // Exclude UNDECIDED
        }
        
        if (count == 0) {
            return 0.0;
        }
        
        return sum / count;
    }
}
