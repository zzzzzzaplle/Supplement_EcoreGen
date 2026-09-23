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
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int unsubmitted = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                unsubmitted++;
            }
        }
        return unsubmitted;
    }

    public double calculateSubmittedReviewAverageScore() {
        int totalScore = 0;
        int submittedCount = 0;
        for (ReviewAssignment assignment : assignments) {
            Grade grade = assignment.getGrade();
            if (grade != Grade.UNDECIDED) {
                submittedCount++;
                if (grade == Grade.ACCEPT) {
                    totalScore += 1;
                } else if (grade == Grade.REJECT) {
                    totalScore += 0;
                }
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedCount;
    }
}
