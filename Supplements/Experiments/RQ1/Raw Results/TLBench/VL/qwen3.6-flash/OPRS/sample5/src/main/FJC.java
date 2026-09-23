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
        if (this.papers.isEmpty()) {
            return 0.0;
        }
        int acceptedCount = 0;
        for (Paper paper : this.papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / this.papers.size();
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
        int count = 0;
        for (ReviewAssignment assignment : this.assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        int processedCount = 0;
        double totalScore = 0.0;

        for (ReviewAssignment assignment : this.assignments) {
            Grade grade = assignment.getGrade();
            if (grade != Grade.UNDECIDED) {
                processedCount++;
                if (grade == Grade.ACCEPT) {
                    totalScore += 1.0;
                } else if (grade == Grade.REJECT) {
                    totalScore += 0.0;
                }
            }
        }

        if (processedCount == 0) {
            return 0.0;
        }

        return totalScore / processedCount;
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
        if (this.reviews.isEmpty()) {
            return true;
        }

        boolean hasAccept = false;
        boolean hasReject = false;

        for (ReviewAssignment review : this.reviews) {
            Grade grade = review.getGrade();
            if (grade == Grade.UNDECIDED) {
                return false; // Consistency check implies submitted reviews only, or fails if undecided exists
            }
            if (grade == Grade.ACCEPT) {
                hasAccept = true;
            } else if (grade == Grade.REJECT) {
                hasReject = true;
            }
        }

        // "exclusively Accept or exclusively Reject"
        if (hasAccept && hasReject) {
            return false;
        }
        
        // If there are reviews, they must be all one type. 
        // If there are no submitted reviews (all undecided), technically not "exclusively" one, but usually consistency implies submitted ones match.
        // Based on "check whether all reviews ... are either exclusively Accept or exclusively Reject", 
        // if there are no submitted reviews, it's ambiguous. But typically, if there are reviews, they must be consistent.
        // Let's assume if there are ANY submitted reviews, they must be all same.
        // If there are NO submitted reviews, is it consistent? 
        // The requirement says "check whether all reviews ... are ...". If set is empty or only undecided, logical vacuum.
        // However, usually this check is done before decision when reviews are expected.
        // Let's stick to: If there are submitted reviews, they must not mix.
        
        return true;
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