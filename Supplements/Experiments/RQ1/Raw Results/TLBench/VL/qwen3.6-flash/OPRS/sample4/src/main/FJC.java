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
        if (role != null && !roles.contains(role)) {
            roles.add(role);
        }
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
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public int countSubmittedPapers() {
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        int total = papers.size();
        if (total == 0) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / total;
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
        if (assignment != null && !assignments.contains(assignment)) {
            assignments.add(assignment);
        }
    }

    public int calculateUnsubmittedReviews() {
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        double totalScore = 0.0;
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            Grade grade = assignment.getGrade();
            if (grade != Grade.UNDECIDED) {
                if (grade == Grade.ACCEPT) {
                    totalScore += 1.0;
                } else if (grade == Grade.REJECT) {
                    totalScore += 0.0;
                }
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return totalScore / count;
    }
}

class CoChair extends UserRole {
    public CoChair() {
        super();
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null) {
            return -1;
        }
        paper.setDecision(decision);
        // Assuming this method returns the number of reviews for the paper or some status code
        // Based on the prompt "Calculate the number of unsubmitted reviews..." logic isn't directly applied here.
        // Returning the total number of reviews associated with the paper as a generic return value 
        // or simply 1 to indicate success. Given the method signature returns int, 
        // and no specific return value is described for makeFinalDecision other than the action,
        // we will return the number of reviews attached to the paper.
        return paper.getReviews().size();
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
            return true;
        }
        
        boolean hasAccept = false;
        boolean hasReject = false;
        
        for (ReviewAssignment review : reviews) {
            Grade grade = review.getGrade();
            if (grade == Grade.ACCEPT) {
                hasAccept = true;
            } else if (grade == Grade.REJECT) {
                hasReject = true;
            }
        }
        
        // "exclusively Accept or exclusively Reject" implies all accepted reviews are ACCEPT 
        // and all rejected reviews are REJECT, but since we only have ACCEPT/REJECT as distinct 
        // submitted grades, consistency means we don't have both ACCEPT and REJECT grades present.
        // Also, strictly speaking, it might mean all reviews must be submitted and be the same.
        // The prompt says "check whether all reviews for a paper ... are either exclusively Accept or exclusively Reject".
        
        return !hasAccept || !hasReject;
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
        if (review != null && !reviews.contains(review)) {
            reviews.add(review);
        }
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