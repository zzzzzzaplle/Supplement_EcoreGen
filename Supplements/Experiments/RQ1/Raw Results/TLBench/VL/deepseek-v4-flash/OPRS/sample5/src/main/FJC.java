import java.util.ArrayList;
import java.util.List;

class User {
    private String name;
    private List<UserRole> roles;

    public User() {
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
        this.papers = new ArrayList<>();
    }

    public void submitPaper(Paper paper) {
        this.papers.add(paper);
    }

    public int countSubmittedPapers() {
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) {
            return 0.0;
        }
        int acceptedCount = 0;
        for (Paper p : papers) {
            if (p.getDecision() == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}

class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int unsubmittedCount = 0;
        for (ReviewAssignment ra : assignments) {
            if (ra.getGrade() == Grade.UNDECIDED) {
                unsubmittedCount++;
            }
        }
        return unsubmittedCount;
    }

    public double calculateSubmittedReviewAverageScore() {
        int totalScore = 0;
        int submittedCount = 0;
        for (ReviewAssignment ra : assignments) {
            if (ra.getGrade() != Grade.UNDECIDED) {
                submittedCount++;
                if (ra.getGrade() == Grade.ACCEPT) {
                    totalScore += 1;
                } else if (ra.getGrade() == Grade.REJECT) {
                    totalScore += 0;
                }
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedCount;
    }

    public void setAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }
}

class CoChair extends UserRole {
    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0; // Return 0 as a simple success indicator
    }
}

enum PaperType {
    RESEARCH,
    EXPERIENCE
}

enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.decision = Grade.UNDECIDED;
        this.reviews = new ArrayList<>();
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        for (ReviewAssignment ra : reviews) {
            if (ra.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (ra.getGrade() == Grade.REJECT) {
                hasReject = true;
            }
            if (hasAccept && hasReject) {
                return false;
            }
        }
        return hasAccept || hasReject;
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

    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
}

class ReviewAssignment {
    private String feedback;
    private Grade grade;

    public ReviewAssignment() {
        this.grade = Grade.UNDECIDED;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}