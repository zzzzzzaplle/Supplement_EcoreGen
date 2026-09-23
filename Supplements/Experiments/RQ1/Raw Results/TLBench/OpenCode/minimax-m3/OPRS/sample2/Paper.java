import java.util.ArrayList;
import java.util.List;

public class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.title = "";
        this.type = PaperType.RESEARCH;
        this.decision = Grade.UNDECIDED;
        this.reviews = new ArrayList<ReviewAssignment>();
    }

    public boolean isAllReviewsConsistent() {
        if (this.reviews == null || this.reviews.size() < 3) {
            return false;
        }
        int acceptCount = 0;
        int rejectCount = 0;
        for (ReviewAssignment ra : this.reviews) {
            if (ra == null) {
                return false;
            }
            if (ra.getGrade() == Grade.ACCEPT) {
                acceptCount++;
            } else if (ra.getGrade() == Grade.REJECT) {
                rejectCount++;
            } else {
                return false;
            }
        }
        return (acceptCount > 0 && rejectCount == 0) || (rejectCount > 0 && acceptCount == 0);
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
        if (review != null) {
            this.reviews.add(review);
        }
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
}
