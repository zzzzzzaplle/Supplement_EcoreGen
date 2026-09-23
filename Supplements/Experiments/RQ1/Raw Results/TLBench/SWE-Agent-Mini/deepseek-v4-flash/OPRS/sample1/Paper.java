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
        boolean allSubmitted = true;

        for (ReviewAssignment review : reviews) {
            Grade g = review.getGrade();
            if (g == Grade.UNDECIDED) {
                allSubmitted = false;
            } else if (g == Grade.ACCEPT) {
                hasAccept = true;
            } else if (g == Grade.REJECT) {
                hasReject = true;
            }
        }

        if (!allSubmitted) {
            return false;
        }

        // All are either exclusively Accept or exclusively Reject
        return !(hasAccept && hasReject);
    }
}
