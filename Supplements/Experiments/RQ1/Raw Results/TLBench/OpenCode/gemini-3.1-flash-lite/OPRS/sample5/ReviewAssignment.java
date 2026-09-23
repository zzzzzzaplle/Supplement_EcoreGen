public class ReviewAssignment {
    private String feedback;
    private Grade grade = Grade.UNDECIDED;

    public ReviewAssignment() {}

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
}
