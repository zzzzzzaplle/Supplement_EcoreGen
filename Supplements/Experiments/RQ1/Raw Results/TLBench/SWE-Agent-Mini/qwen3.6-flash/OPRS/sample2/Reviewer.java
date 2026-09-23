import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new java.util.ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        if (assignment != null) {
            this.assignments.add(assignment);
        }
    }

    public int calculateUnsubmittedReviews() {
        if (this.assignments == null || this.assignments.isEmpty()) {
            return 0;
        }

        int unsubmittedCount = 0;
        for (ReviewAssignment assignment : this.assignments) {
            if (assignment == null || assignment.getGrade() == null || assignment.getGrade() == Grade.UNDECIDED) {
                unsubmittedCount++;
            }
        }
        return unsubmittedCount;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (this.assignments == null || this.assignments.isEmpty()) {
            return 0.0;
        }

        int submittedCount = 0;
        double totalScore = 0.0;

        for (ReviewAssignment assignment : this.assignments) {
            if (assignment == null) {
                continue;
            }
            Grade grade = assignment.getGrade();
            if (grade == null || grade == Grade.UNDECIDED) {
                continue;
            }
            
            submittedCount++;
            if (grade == Grade.ACCEPT) {
                totalScore += 1.0;
            } else if (grade == Grade.REJECT) {
                totalScore += 0.0;
            }
        }

        if (submittedCount == 0) {
            return 0.0;
        }

        return totalScore / submittedCount;
    }
}
