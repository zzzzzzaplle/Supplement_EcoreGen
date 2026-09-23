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
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
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
            if (review.getGrade() == null || review.getGrade() == Grade.UNDECIDED) {
                continue;
            }
            if (review.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (review.getGrade() == Grade.REJECT) {
                hasReject = true;
            }
        }
        
        // All reviews are exclusively Accept OR exclusively Reject
        return (hasAccept && !hasReject) || (!hasAccept && hasReject);
    }
}
