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
    
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
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
        return papers.size();
    }
    
    public double calculateAcceptanceRate() {
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / papers.size();
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
    
    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }
    
    public void addReviewAssignment(ReviewAssignment assignment) {
        this.assignments.add(assignment);
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
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int submittedCount = 0;
        int totalScore = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment.getGrade() == Grade.ACCEPT) {
                submittedCount++;
                totalScore += 1;
            } else if (assignment.getGrade() == Grade.REJECT) {
                submittedCount++;
                totalScore += 0;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) totalScore / submittedCount;
    }
}

class CoChair extends UserRole {
    
    public CoChair() {
    }
    
    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
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
    
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        boolean hasAccept = false;
        boolean hasReject = false;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT) {
                hasAccept = true;
            } else if (review.getGrade() == Grade.REJECT) {
                hasReject = true;
            } else {
                return false;
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