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
            this.reviews = new ArrayList<ReviewAssignment>();
        }
        this.reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }

    public boolean isAllReviewsConsistent() {
        if (this.reviews == null || this.reviews.size() < 3) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        boolean hasUndecided = false;
        for (ReviewAssignment ra : this.reviews) {
            if (ra == null) {
                continue;
            }
            Grade g = ra.getGrade();
            if (g == Grade.ACCEPT) {
                hasAccept = true;
            } else if (g == Grade.REJECT) {
                hasReject = true;
            } else {
                hasUndecided = true;
            }
        }
        if (hasUndecided) {
            return false;
        }
        if (hasAccept && !hasReject) {
            return true;
        }
        if (hasReject && !hasAccept) {
            return true;
        }
        return false;
    }
}
