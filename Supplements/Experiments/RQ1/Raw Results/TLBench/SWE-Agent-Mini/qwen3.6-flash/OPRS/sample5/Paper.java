import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.decision = Grade.UNDECIDED;
        this.reviews = new java.util.ArrayList<>();
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.isEmpty()) {
            return true;
        }
        
        boolean hasAccept = false;
        boolean hasReject = false;
        
        for (ReviewAssignment review : reviews) {
            Grade grade = review.getGrade();
            if (grade != Grade.UNDECIDED) {
                if (grade == Grade.ACCEPT) {
                    hasAccept = true;
                } else if (grade == Grade.REJECT) {
                    hasReject = true;
                }
            }
        }
        
        // Consistent means all submitted reviews are either exclusively Accept or exclusively Reject
        return !(hasAccept && hasReject);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PaperType getType() {
        return type;
    }

    public void setType(PaperType type) {
        this.type = type;
    }

    public Grade getDecision() {
        return decision;
    }

    public void setDecision(Grade decision) {
        this.decision = decision;
    }

    public void addReview(ReviewAssignment review) {
        this.reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }
}
