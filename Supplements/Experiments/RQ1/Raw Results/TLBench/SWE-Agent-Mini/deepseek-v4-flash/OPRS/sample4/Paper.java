import java.util.List;
import java.util.ArrayList;

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
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }
    
    public List<ReviewAssignment> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
    
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        for (ReviewAssignment review : reviews) {
            Grade g = review.getGrade();
            if (g == Grade.ACCEPT) {
                hasAccept = true;
            } else if (g == Grade.REJECT) {
                hasReject = true;
            }
        }
        // All reviews are either exclusively Accept or exclusively Reject
        return hasAccept != hasReject;
    }
}
