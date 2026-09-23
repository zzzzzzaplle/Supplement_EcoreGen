import java.util.ArrayList;
import java.util.List;

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
        reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.isEmpty()) {
            return true;
        }
        Grade firstGrade = null;
        for (ReviewAssignment review : reviews) {
            Grade grade = review.getGrade();
            if (grade == Grade.ACCEPT || grade == Grade.REJECT) {
                if (firstGrade == null) {
                    firstGrade = grade;
                } else if (grade != firstGrade) {
                    return false;
                }
            }
        }
        return true;
    }
}
