import java.util.List;
import java.util.ArrayList;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
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

    /**
     * Check whether all reviews for a paper are either exclusively Accept 
     * or exclusively Reject.
     * Returns true if all submitted reviews are consistent (all ACCEPT or all REJECT).
     * Returns false otherwise or if there are no reviews or undecided reviews.
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.isEmpty()) {
            return false;
        }
        
        boolean hasAccept = false;
        boolean hasReject = false;
        
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (review.getGrade() == Grade.REJECT) {
                hasReject = true;
            }
            // UNDECIDED breaks consistency or is ignored? 
            // The requirement says "all reviews ... are either exclusively Accept or exclusively Reject".
            // If any review is UNDECIDED, it's not exclusively Accept or Reject.
            if (review.getGrade() == Grade.UNDECIDED) {
                return false;
            }
        }
        
        // If both Accept and Reject exist, it's not consistent
        return !(hasAccept && hasReject) && (hasAccept || hasReject);
    }
}
