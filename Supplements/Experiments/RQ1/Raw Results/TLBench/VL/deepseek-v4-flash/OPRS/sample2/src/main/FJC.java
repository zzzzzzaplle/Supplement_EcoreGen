import java.util.ArrayList;
import java.util.List;

class User {
    private String name;
    private List<UserRole> roles = new ArrayList<>();

    public User() {
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
    private List<Paper> papers = new ArrayList<>();

    public Author() {
    }

    public void submitPaper(Paper paper) {
        this.papers.add(paper);
    }

    public int countSubmittedPapers() {
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper p : papers) {
            if (p.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }
}

class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments = new ArrayList<>();

    public Reviewer() {
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        int unsubmitted = 0;
        for (ReviewAssignment ra : assignments) {
            if (ra.getGrade() == Grade.UNDECIDED) {
                unsubmitted++;
            }
        }
        return unsubmitted;
    }

    public double calculateSubmittedReviewAverageScore() {
        int total = 0;
        int count = 0;
        for (ReviewAssignment ra : assignments) {
            Grade grade = ra.getGrade();
            if (grade != Grade.UNDECIDED) {
                count++;
                if (grade == Grade.ACCEPT) {
                    total += 1;
                } else if (grade == Grade.REJECT) {
                    total += 0;
                }
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return (double) total / count;
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
    private List<ReviewAssignment> reviews = new ArrayList<>();

    public Paper() {
        this.decision = Grade.UNDECIDED;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews.size() < 3) {
            return false;
        }
        Grade firstSubmitted = null;
        for (ReviewAssignment ra : reviews) {
            Grade grade = ra.getGrade();
            if (grade == Grade.UNDECIDED) {
                return false;
            }
            if (firstSubmitted == null) {
                firstSubmitted = grade;
            } else if (grade != firstSubmitted) {
                return false;
            }
        }
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