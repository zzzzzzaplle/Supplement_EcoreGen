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

    // Functional Requirement 2: Check if all reviews are exclusively Accept or exclusively Reject.
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.isEmpty()) {
            return false;
        }

        boolean hasAccept = false;
        boolean hasReject = false;

        for (ReviewAssignment ra : reviews) {
            if (ra == null) {
                continue;
            }
            if (ra.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (ra.getGrade() == Grade.REJECT) {
                hasReject = true;
            } else {
                // UNDECIDED means it's not exclusively accept or reject yet
                return false;
            }
        }

        // If we have both, it's not consistent.
        if (hasAccept && hasReject) {
            return false;
        }

        // Must have at least one decision
        return hasAccept || hasReject;
    }
}
