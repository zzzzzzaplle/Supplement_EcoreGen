import java.util.*;

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
        if (reviews == null || reviews.size() < 3) {
            return false;
        }

        Boolean allAccept = null;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.UNDECIDED) {
                return false;
            }
            if (allAccept == null) {
                allAccept = (review.getGrade() == Grade.ACCEPT);
            } else {
                if (allAccept && review.getGrade() != Grade.ACCEPT) {
                    return false;
                }
                if (!allAccept && review.getGrade() != Grade.REJECT) {
                    return false;
                }
            }
        }
        return true;
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
