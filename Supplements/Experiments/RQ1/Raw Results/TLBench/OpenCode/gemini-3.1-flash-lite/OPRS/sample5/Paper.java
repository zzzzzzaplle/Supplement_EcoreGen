public class Paper {
    private String title;
    private PaperType type;
    private Grade decision = Grade.UNDECIDED;
    private java.util.List<ReviewAssignment> reviews = new java.util.ArrayList<>();

    public Paper() {}

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) return false;
        long accepts = reviews.stream().filter(r -> r.getGrade() == Grade.ACCEPT).count();
        long rejects = reviews.stream().filter(r -> r.getGrade() == Grade.REJECT).count();
        return accepts == reviews.size() || rejects == reviews.size();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public PaperType getType() { return type; }
    public void setType(PaperType type) { this.type = type; }
    public Grade getDecision() { return decision; }
    public void setDecision(Grade decision) { this.decision = decision; }
    public void addReview(ReviewAssignment review) { this.reviews.add(review); }
    public java.util.List<ReviewAssignment> getReviews() { return reviews; }
}
