import java.util.List;
import java.util.ArrayList;

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
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        assignments.add(assignment);
    }
    
    public int calculateUnsubmittedReviews() {
        if (assignments == null || assignments.isEmpty()) {
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
    
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int totalScore = 0;
        int submittedCount = 0;
        for (ReviewAssignment assignment : assignments) {
            Grade g = assignment.getGrade();
            if (g == Grade.ACCEPT) {
                totalScore += 1;
                submittedCount++;
            } else if (g == Grade.REJECT) {
                totalScore += 0;
                submittedCount++;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedCount;
    }
}
