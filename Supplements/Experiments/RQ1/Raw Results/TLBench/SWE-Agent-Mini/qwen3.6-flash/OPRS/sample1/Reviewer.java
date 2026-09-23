import java.util.List;

public class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        if (this.assignments == null) {
            this.assignments = new java.util.ArrayList<>();
        }
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        if (assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == null || assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null) {
            return 0.0;
        }
        
        double totalScore = 0.0;
        int reviewCount = 0;
        
        for (ReviewAssignment assignment : assignments) {
            Grade grade = assignment.getGrade();
            if (grade == null || grade == Grade.UNDECIDED) {
                continue;
            }
            
            if (grade == Grade.ACCEPT) {
                totalScore += 1.0;
            } else if (grade == Grade.REJECT) {
                totalScore += 0.0;
            }
            reviewCount++;
        }
        
        if (reviewCount == 0) {
            return 0.0;
        }
        
        return totalScore / reviewCount;
    }
}
