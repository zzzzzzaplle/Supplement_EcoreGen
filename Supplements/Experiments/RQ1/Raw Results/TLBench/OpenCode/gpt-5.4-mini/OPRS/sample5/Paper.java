import java.util.ArrayList;
import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<ReviewAssignment>();
        this.decision = Grade.UNDECIDED;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        Grade first = null;
        for (ReviewAssignment review : reviews) {
            if (review == null || review.getGrade() == null || review.getGrade() == Grade.UNDECIDED) {
                return false;
            }
            if (first == null) {
                first = review.getGrade();
            } else if (first != review.getGrade()) {
                return false;
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
        if (reviews == null) {
            reviews = new ArrayList<ReviewAssignment>();
        }
        reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }
}
