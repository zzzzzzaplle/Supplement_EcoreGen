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
        if (this.papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : this.papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / this.papers.size();
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
        int count = 0;
        for (ReviewAssignment assignment : this.assignments) {
            if (assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }
    
    public double calculateSubmittedReviewAverageScore() {
        int submittedCount = 0;
        int acceptCount = 0;
        for (ReviewAssignment assignment : this.assignments) {
            Grade grade = assignment.getGrade();
            if (grade == Grade.ACCEPT) {
                acceptCount++;
                submittedCount++;
            } else if (grade == Grade.REJECT) {
                submittedCount++;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) acceptCount / submittedCount;
    }
}

class CoChair extends UserRole {
    
    public CoChair() {
    }
    
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper.isAllReviewsConsistent()) {
            paper.setDecision(decision);
            return 1;
        }
        return 0;
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
        if (this.reviews.size() < 3) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        for (ReviewAssignment review : this.reviews) {
            Grade grade = review.getGrade();
            if (grade == Grade.UNDECIDED) {
                return false;
            } else if (grade == Grade.ACCEPT) {
                hasAccept = true;
            } else if (grade == Grade.REJECT) {
                hasReject = true;
            }
        }
        return (hasAccept && !hasReject) || (!hasAccept && hasReject);
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