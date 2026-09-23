import java.util.ArrayList;
import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) {
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
            // Ignore UNDECIDED? Requirements say "are either exclusively Accept or exclusively reject"
            // Assuming UNDECIDED means not all reviews are finished.
            if (review.getGrade() == Grade.UNDECIDED) {
                return false; 
            }
        }
        
        // Return true if (all ACCEPT) OR (all REJECT)
        return (hasAccept && !hasReject) || (!hasAccept && hasReject);
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

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public void addReview(ReviewAssignment review) {
        this.reviews.add(review);
    }
}
