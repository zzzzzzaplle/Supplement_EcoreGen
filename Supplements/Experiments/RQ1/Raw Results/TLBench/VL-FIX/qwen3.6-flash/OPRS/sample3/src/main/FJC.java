import java.util.ArrayList;
import java.util.List;

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

    public List<Paper> getPapers() {
        return papers;
    }

    public void submitPaper(Paper paper) {
        this.papers.add(paper);
    }

    public int countSubmittedPapers() {
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        int totalPapers = this.papers.size();
        if (totalPapers == 0) {
            return 0.0;
        }
        int acceptedPapers = 0;
        for (Paper paper : this.papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                acceptedPapers++;
            }
        }
        return (double) acceptedPapers / totalPapers;
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
        int count = 0;
        for (ReviewAssignment assignment : this.assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        int acceptedCount = 0;
        int rejectedCount = 0;
        int totalSubmitted = 0;

        for (ReviewAssignment assignment : this.assignments) {
            if (assignment.getGrade() != Grade.UNDECIDED) {
                totalSubmitted++;
                if (assignment.getGrade() == Grade.ACCEPT) {
                    acceptedCount++;
                } else if (assignment.getGrade() == Grade.REJECT) {
                    rejectedCount++;
                }
            }
        }

        if (totalSubmitted == 0) {
            return 0.0;
        }

        return (double) acceptedCount / totalSubmitted;
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
        } else if (decision == Grade.REJECT) {
            return 0;
        } else {
            return -1;
        }
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
        this.title = "";
        this.type = PaperType.RESEARCH;
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
        this.reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public boolean isAllReviewsConsistent() {
        if (this.reviews == null || this.reviews.isEmpty()) {
            return false;
        }

        boolean allAccept = true;
        boolean allReject = true;

        for (ReviewAssignment review : this.reviews) {
            if (review.getGrade() == Grade.UNDECIDED) {
                return false;
            }
            if (review.getGrade() != Grade.ACCEPT) {
                allAccept = false;
            }
            if (review.getGrade() != Grade.REJECT) {
                allReject = false;
            }
        }

        return allAccept || allReject;
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