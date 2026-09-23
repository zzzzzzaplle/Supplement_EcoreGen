public class Reviewer extends UserRole {
    private java.util.List<ReviewAssignment> assignments = new java.util.ArrayList<>();

    public Reviewer() {}

    public java.util.List<ReviewAssignment> getReviewAssignments() { return assignments; }
    public void addReviewAssignment(ReviewAssignment assignment) { this.assignments.add(assignment); }

    public int calculateUnsubmittedReviews() {
        return (int) assignments.stream()
            .filter(a -> a.getGrade() == Grade.UNDECIDED)
            .count();
    }

    public double calculateSubmittedReviewAverageScore() {
        java.util.List<ReviewAssignment> submitted = assignments.stream()
            .filter(a -> a.getGrade() != Grade.UNDECIDED)
            .collect(java.util.stream.Collectors.toList());
        if (submitted.isEmpty()) return 0.0;
        long acceptCount = submitted.stream()
            .filter(a -> a.getGrade() == Grade.ACCEPT)
            .count();
        return (double) acceptCount / submitted.size();
    }
}
