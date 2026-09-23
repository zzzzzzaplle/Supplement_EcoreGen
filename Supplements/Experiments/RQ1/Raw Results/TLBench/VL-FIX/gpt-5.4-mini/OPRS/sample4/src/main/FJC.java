import java.util.ArrayList;
import java.util.List;

/**
 * Represents a system user who can have one or more roles.
 */
class User {
    private String name;
    private List<UserRole> roles;

    /**
     * Creates an empty User instance.
     */
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

    /**
     * Adds a role to this user.
     *
     * @param role the role to add
     */
    public void addRole(UserRole role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }
}

/**
 * Base class for user roles.
 */
abstract class UserRole {
    private String id;

    /**
     * Creates an empty UserRole instance.
     */
    public UserRole() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

/**
 * Represents an author role.
 */
class Author extends UserRole {
    private List<Paper> papers;

    /**
     * Creates an empty Author instance.
     */
    public Author() {
        this.papers = new ArrayList<>();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    /**
     * Submits a paper by adding it to the author's paper list.
     *
     * @param paper the paper to submit
     */
    public void submitPaper(Paper paper) {
        if (this.papers == null) {
            this.papers = new ArrayList<>();
        }
        this.papers.add(paper);
    }

    /**
     * Counts the number of submitted papers.
     *
     * @return the number of papers submitted by the author
     */
    public int countSubmittedPapers() {
        return papers == null ? 0 : papers.size();
    }

    /**
     * Calculates the acceptance rate of this author's submitted papers.
     * Accepted papers are those with final decision ACCEPT.
     *
     * @return acceptance rate in range [0.0, 1.0]
     */
    public double calculateAcceptanceRate() {
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }

        int accepted = 0;
        for (Paper paper : papers) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / papers.size();
    }
}

/**
 * Represents a reviewer role.
 */
class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    /**
     * Creates an empty Reviewer instance.
     */
    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Adds a review assignment to this reviewer.
     *
     * @param assignment the assignment to add
     */
    public void addReviewAssignment(ReviewAssignment assignment) {
        if (this.assignments == null) {
            this.assignments = new ArrayList<>();
        }
        this.assignments.add(assignment);
    }

    /**
     * Calculates the number of unsubmitted reviews for this reviewer.
     * A review is unsubmitted if its grade is UNDECIDED or missing.
     *
     * @return number of unsubmitted reviews
     */
    public int calculateUnsubmittedReviews() {
        if (assignments == null || assignments.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment == null || assignment.getGrade() == null || assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the average score of submitted review grades.
     * ACCEPT = 1, REJECT = 0, UNDECIDED is excluded.
     *
     * @return average submitted review score in range [0.0, 1.0]
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }

        int submittedCount = 0;
        int scoreSum = 0;

        for (ReviewAssignment assignment : assignments) {
            if (assignment == null || assignment.getGrade() == null) {
                continue;
            }

            Grade grade = assignment.getGrade();
            if (grade == Grade.UNDECIDED) {
                continue;
            }

            submittedCount++;
            if (grade == Grade.ACCEPT) {
                scoreSum += 1;
            }
        }

        if (submittedCount == 0) {
            return 0.0;
        }

        return (double) scoreSum / submittedCount;
    }
}

/**
 * Represents a co-chair role.
 */
class CoChair extends UserRole {
    /**
     * Creates an empty CoChair instance.
     */
    public CoChair() {
    }

    /**
     * Makes the final decision for a paper.
     *
     * @param paper    the paper to decide on
     * @param decision the final decision
     * @return 1 for ACCEPT, 0 for REJECT or UNDECIDED/null
     */
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null) {
            return 0;
        }
        paper.setDecision(decision);
        return decision == Grade.ACCEPT ? 1 : 0;
    }
}

/**
 * Paper type enumeration.
 */
enum PaperType {
    RESEARCH,
    EXPERIENCE
}

/**
 * Grade enumeration for reviews and final decision.
 */
enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

/**
 * Represents a paper submitted by an author and reviewed by reviewers.
 */
class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    /**
     * Creates an empty Paper instance.
     */
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

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }

    /**
     * Adds a review assignment to this paper.
     *
     * @param review the review assignment to add
     */
    public void addReview(ReviewAssignment review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }

    /**
     * Checks whether all submitted reviews for this paper are consistent.
     * Consistent means at least three reviews exist and all submitted grades
     * are exclusively ACCEPT or exclusively REJECT.
     *
     * @return true if all reviews are consistent; false otherwise
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }

        Grade expected = null;
        int submittedCount = 0;

        for (ReviewAssignment review : reviews) {
            if (review == null || review.getGrade() == null || review.getGrade() == Grade.UNDECIDED) {
                return false;
            }

            Grade current = review.getGrade();
            if (expected == null) {
                expected = current;
            } else if (expected != current) {
                return false;
            }
            submittedCount++;
        }

        return submittedCount >= 3;
    }
}

/**
 * Represents a review assignment for a reviewer.
 */
class ReviewAssignment {
    private String feedback;
    private Grade grade;

    /**
     * Creates an empty ReviewAssignment instance.
     */
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