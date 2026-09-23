import java.util.ArrayList;
import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.decision = Grade.UNDECIDED;
        this.reviews = new ArrayList<>();
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

    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
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
        }
        // Check if all submitted reviews (those not UNDECIDED) are consistent
        // But the requirement says: check whether all reviews for a paper (from at least three reviewers) 
        // are either exclusively Accept or exclusively Reject
        
        // Count submitted reviews
        int submittedCount = 0;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT || review.getGrade() == Grade.REJECT) {
                submittedCount++;
            }
        }
        
        if (submittedCount < 3) {
            return false;
        }
        
        return hasAccept != hasReject; // true if only one type appears
    }
}
