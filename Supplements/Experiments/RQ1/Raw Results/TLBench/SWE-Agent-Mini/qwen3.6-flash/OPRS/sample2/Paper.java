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
        if (review != null) {
            this.reviews.add(review);
        }
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.isEmpty()) {
            return false;
        }

        boolean hasAccept = false;
        boolean hasReject = false;

        for (ReviewAssignment review : reviews) {
            if (review == null) {
                continue;
            }
            Grade grade = review.getGrade();
            if (grade == null || grade == Grade.UNDECIDED) {
                // If any review is undecided, they are not exclusively accept or reject
                return false;
            }
            if (grade == Grade.ACCEPT) {
                hasAccept = true;
            } else if (grade == Grade.REJECT) {
                hasReject = true;
            }
        }

        // Check if all reviews are exclusively Accept or exclusively Reject
        // This means either (all accept AND no reject) OR (all reject AND no accept)
        if (hasAccept && hasReject) {
            return false;
        }

        return hasAccept != hasReject;
    }
}
