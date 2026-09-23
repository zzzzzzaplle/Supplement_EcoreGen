import java.util.ArrayList;
import java.util.List;

/**
 * Represents a system user, such as an author, reviewer, or co-chair.
 */
class User {
    private String name;
    private List<UserRole> roles;

    /**
     * Unparameterized constructor.
     */
    public User() {
        this.roles = new ArrayList<>();
    }

    /**
     * Gets the user's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of roles associated with this user.
     *
     * @return the roles
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles associated with this user.
     *
     * @param roles the roles to set
     */
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
 * Abstract base class for roles a user can have.
 */
abstract class UserRole {
    private String id;

    /**
     * Unparameterized constructor.
     */
    public UserRole() {
    }

    /**
     * Gets the role identifier.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the role identifier.
     *
     * @param id the id to set
     */
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
     * Unparameterized constructor.
     */
    public Author() {
        this.papers = new ArrayList<>();
    }

    /**
     * Submits a paper.
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
     * Counts the total number of submitted papers.
     *
     * @return number of papers
     */
    public int countSubmittedPapers() {
        return papers == null ? 0 : papers.size();
    }

    /**
     * Calculates the acceptance rate for the author's papers.
     *
     * @return acceptance rate between 0.0 and 1.0
     */
    public double calculateAcceptanceRate() {
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        int total = 0;
        for (Paper paper : papers) {
            if (paper != null && paper.getDecision() != null && paper.getDecision() != Grade.UNDECIDED) {
                total++;
                if (paper.getDecision() == Grade.ACCEPT) {
                    accepted++;
                }
            }
        }
        return total == 0 ? 0.0 : (double) accepted / total;
    }

    /**
     * Gets the list of submitted papers.
     *
     * @return the papers
     */
    public List<Paper> getPapers() {
        return papers;
    }

    /**
     * Sets the list of submitted papers.
     *
     * @param papers the papers to set
     */
    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}

/**
 * Represents a reviewer role.
 */
class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    /**
     * Unparameterized constructor.
     */
    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    /**
     * Gets the review assignments for this reviewer.
     *
     * @return the assignments
     */
    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    /**
     * Sets the review assignments for this reviewer.
     *
     * @param assignments the assignments to set
     */
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
     * A review is considered unsubmitted if its grade is UNDECIDED.
     *
     * @return number of unsubmitted reviews
     */
    public int calculateUnsubmittedReviews() {
        if (assignments == null || assignments.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment != null && assignment.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the average score of submitted reviews only.
     * ACCEPT is treated as 1 and REJECT as 0.
     *
     * @return average score between 0.0 and 1.0
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int submittedCount = 0;
        int sum = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment != null && assignment.getGrade() != null && assignment.getGrade() != Grade.UNDECIDED) {
                submittedCount++;
                if (assignment.getGrade() == Grade.ACCEPT) {
                    sum += 1;
                }
            }
        }
        return submittedCount == 0 ? 0.0 : (double) sum / submittedCount;
    }
}

/**
 * Represents a co-chair role.
 */
class CoChair extends UserRole {
    /**
     * Unparameterized constructor.
     */
    public CoChair() {
    }

    /**
     * Makes the final decision for a paper.
     *
     * @param paper the paper under review
     * @param decision the final decision
     * @return 1 if accepted, 0 otherwise
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
 * Enumerates the possible paper types.
 */
enum PaperType {
    RESEARCH,
    EXPERIENCE
}

/**
 * Enumerates the possible review grades and decisions.
 */
enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

/**
 * Represents a paper submitted by authors and reviewed by reviewers.
 */
class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    /**
     * Unparameterized constructor.
     */
    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
    }

    /**
     * Determines whether all reviews are consistent and there are at least three reviews.
     * Consistent means all submitted reviews are exclusively ACCEPT or exclusively REJECT.
     *
     * @return true if consistent and at least three reviews; false otherwise
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        Grade firstSubmitted = null;
        int submittedCount = 0;
        for (ReviewAssignment review : reviews) {
            if (review != null && review.getGrade() != null && review.getGrade() != Grade.UNDECIDED) {
                submittedCount++;
                if (firstSubmitted == null) {
                    firstSubmitted = review.getGrade();
                } else if (review.getGrade() != firstSubmitted) {
                    return false;
                }
            }
        }
        return submittedCount >= 3;
    }

    /**
     * Gets the paper title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the paper title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the paper type.
     *
     * @return the type
     */
    public PaperType getType() {
        return type;
    }

    /**
     * Sets the paper type.
     *
     * @param type the type to set
     */
    public void setType(PaperType type) {
        this.type = type;
    }

    /**
     * Gets the final decision for the paper.
     *
     * @return the decision
     */
    public Grade getDecision() {
        return decision;
    }

    /**
     * Sets the final decision for the paper.
     *
     * @param decision the decision to set
     */
    public void setDecision(Grade decision) {
        this.decision = decision;
    }

    /**
     * Adds a review assignment to this paper.
     *
     * @param review the review to add
     */
    public void addReview(ReviewAssignment review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }

    /**
     * Gets the list of reviews for this paper.
     *
     * @return the reviews
     */
    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    /**
     * Sets the list of reviews for this paper.
     *
     * @param reviews the reviews to set
     */
    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
}

/**
 * Represents a review assignment with feedback and grade.
 */
class ReviewAssignment {
    private String feedback;
    private Grade grade;

    /**
     * Unparameterized constructor.
     */
    public ReviewAssignment() {
        this.grade = Grade.UNDECIDED;
    }

    /**
     * Gets the review feedback.
     *
     * @return the feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the review feedback.
     *
     * @param feedback the feedback to set
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Gets the review grade.
     *
     * @return the grade
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Sets the review grade.
     *
     * @param grade the grade to set
     */
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}