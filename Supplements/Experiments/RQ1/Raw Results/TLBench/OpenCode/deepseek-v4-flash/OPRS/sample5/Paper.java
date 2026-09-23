import java.util.ArrayList;
import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<ReviewAssignment>();
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) {
            return false;
        }
        int acceptCount = 0;
        int rejectCount = 0;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT) {
                acceptCount++;
            } else if (review.getGrade() == Grade.REJECT) {
                rejectCount++;
            }
        }
        if (acceptCount > 0 && rejectCount > 0) {
            return false;
        }
        return acceptCount > 0 || rejectCount > 0;
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
