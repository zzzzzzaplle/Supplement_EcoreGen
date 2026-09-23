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
        Grade target = null;
        int submittedCount = 0;
        for (ReviewAssignment review : reviews) {
            if (review != null) {
                Grade grade = review.getGrade();
                if (grade == Grade.ACCEPT || grade == Grade.REJECT) {
                    submittedCount++;
                    if (target == null) {
                        target = grade;
                    } else if (target != grade) {
                        return false;
                    }
                }
            }
        }
        return submittedCount >= 3 && target != null;
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
}
