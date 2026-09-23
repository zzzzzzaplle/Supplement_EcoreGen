import java.util.ArrayList;
import java.util.List;

enum PaperType {
    RESEARCH,
    EXPERIENCE
}

enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

class User {
    private String name;
    private List<UserRole> roles;

    public User() {
        this.name = "";
        this.roles = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void addRole(UserRole role) {
        this.roles.add(role);
    }
}

abstract class UserRole {
    private String id;

    public UserRole() {
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        super();
        this.papers = new ArrayList<>();
    }

    public void submitPaper(Paper paper) {
        this.papers.add(paper);
    }

    public int countSubmittedPapers() {
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        int total = countSubmittedPapers();
        if (total == 0) {
            return 0.0;
        }
        int acceptedCount = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / total;
    }

    public List<Paper> getPapers() {
        return papers;
    }
}

class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        super();
        this.assignments = new ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int unsubmitted = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                unsubmitted++;
            }
        }
        return unsubmitted;
    }

    public double calculateSubmittedReviewAverageScore() {
        int count = 0;
        double sum = 0.0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() != Grade.UNDECIDED) {
                sum += (assignment.getGrade() == Grade.ACCEPT ? 1.0 : 0.0);
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }
}

class CoChair extends UserRole {
    public CoChair() {
        super();
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        if (decision == Grade.ACCEPT) {
            return 1;
        } else {
            return 0;
        }
    }
}

class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.title = "";
        this.type = PaperType.RESEARCH;
        this.decision = Grade.UNDECIDED;
        this.reviews = new ArrayList<>();
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.isEmpty()) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (review.getGrade() == Grade.REJECT) {
                hasReject = true;
            }
        }
        return (hasAccept && !hasReject) || (!hasAccept && hasReject);
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

class ReviewAssignment {
    private String feedback;
    private Grade grade;

    public ReviewAssignment() {
        this.feedback = "";
        this.grade = Grade.UNDECIDED;
    }

    public String getFeedback() {
        return feedback;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}