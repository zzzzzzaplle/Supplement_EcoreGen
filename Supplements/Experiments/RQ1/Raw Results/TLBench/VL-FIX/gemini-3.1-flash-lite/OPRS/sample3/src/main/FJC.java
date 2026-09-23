import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

enum PaperType {
    RESEARCH, EXPERIENCE
}

enum Grade {
    UNDECIDED, ACCEPT, REJECT
}

class User {
    private String name;
    private List<UserRole> roles;

    public User() {
        this.roles = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<UserRole> getRoles() { return roles; }
    public void addRole(UserRole role) { this.roles.add(role); }
}

abstract class UserRole {
    private String id;

    public UserRole() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<>();
    }

    public void submitPaper(Paper paper) { this.papers.add(paper); }
    
    public int countSubmittedPapers() {
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) return 0.0;
        long acceptedCount = papers.stream().filter(p -> p.getDecision() == Grade.ACCEPT).count();
        return (double) acceptedCount / papers.size();
    }

    public List<Paper> getPapers() { return papers; }
    public void setPapers(List<Paper> papers) { this.papers = papers; }
}

class ReviewAssignment {
    private String feedback;
    private Grade grade;

    public ReviewAssignment() {
        this.grade = Grade.UNDECIDED;
    }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
}

class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public int calculateUnsubmittedReviews() {
        return (int) assignments.stream().filter(a -> a.getGrade() == Grade.UNDECIDED).count();
    }

    public double calculateSubmittedReviewAverageScore() {
        List<ReviewAssignment> submitted = assignments.stream()
                .filter(a -> a.getGrade() != Grade.UNDECIDED)
                .collect(Collectors.toList());
        if (submitted.isEmpty()) return 0.0;
        long accepts = submitted.stream().filter(a -> a.getGrade() == Grade.ACCEPT).count();
        return (double) accepts / submitted.size();
    }

    public List<ReviewAssignment> getReviewAssignments() { return assignments; }
    public void addReviewAssignment(ReviewAssignment assignment) { this.assignments.add(assignment); }
}

class CoChair extends UserRole {
    public CoChair() {}
    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0;
    }
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
        if (reviews.size() < 3) return false;
        boolean allAccept = reviews.stream().allMatch(r -> r.getGrade() == Grade.ACCEPT);
        boolean allReject = reviews.stream().allMatch(r -> r.getGrade() == Grade.REJECT);
        return allAccept || allReject;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public PaperType getType() { return type; }
    public void setType(PaperType type) { this.type = type; }
    public Grade getDecision() { return decision; }
    public void setDecision(Grade decision) { this.decision = decision; }
    public void addReview(ReviewAssignment review) { this.reviews.add(review); }
    public List<ReviewAssignment> getReviews() { return reviews; }
}