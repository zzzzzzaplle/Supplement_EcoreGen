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

class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public PaperType getType() { return type; }
    public void setType(PaperType type) { this.type = type; }
    public Grade getDecision() { return decision; }
    public void setDecision(Grade decision) { this.decision = decision; }
    public List<ReviewAssignment> getReviews() { return reviews; }
    public void addReview(ReviewAssignment review) { this.reviews.add(review); }

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) return false;
        List<Grade> submitted = reviews.stream()
                .map(ReviewAssignment::getGrade)
                .filter(g -> g != Grade.UNDECIDED)
                .collect(Collectors.toList());
        if (submitted.size() != reviews.size()) return false;
        return submitted.stream().allMatch(g -> g == Grade.ACCEPT) ||
               submitted.stream().allMatch(g -> g == Grade.REJECT);
    }
}

class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<>();
    }

    public List<Paper> getPapers() { return papers; }
    public void submitPaper(Paper paper) { this.papers.add(paper); }

    public int countSubmittedPapers() { return papers.size(); }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) return 0.0;
        long accepted = papers.stream().filter(p -> p.getDecision() == Grade.ACCEPT).count();
        return (double) accepted / papers.size();
    }
}

class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() { return assignments; }
    public void addReviewAssignment(ReviewAssignment assignment) { this.assignments.add(assignment); }

    public int calculateUnsubmittedReviews() {
        return (int) assignments.stream().filter(a -> a.getGrade() == Grade.UNDECIDED).count();
    }

    public double calculateSubmittedReviewAverageScore() {
        List<ReviewAssignment> submitted = assignments.stream()
                .filter(a -> a.getGrade() != Grade.UNDECIDED)
                .collect(Collectors.toList());
        if (submitted.isEmpty()) return 0.0;
        double sum = submitted.stream()
                .mapToDouble(a -> a.getGrade() == Grade.ACCEPT ? 1.0 : 0.0)
                .sum();
        return sum / submitted.size();
    }
}

class CoChair extends UserRole {
    public CoChair() {}

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 1;
    }
}