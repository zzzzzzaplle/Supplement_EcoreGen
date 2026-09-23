import java.util.ArrayList;
import java.util.List;

/**
 * Represents a system user with a name and one or more roles.
 */
class User {
    private String name;
    private List<UserRole> roles;

    /**
     * Creates an empty user.
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
     * Gets the list of roles for this user.
     *
     * @return the roles list
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles for this user.
     *
     * @param roles the roles list to set
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
 * Base class for all user roles.
 */
abstract class UserRole {
    private String id;

    /**
     * Creates a role with no identifier.
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
 * Represents an author who can submit papers.
 */
class Author extends UserRole {
    private List<Paper> papers;

    /**
     * Creates an author with an empty paper list.
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
     * Calculates the acceptance rate of the author's papers.
     * Accepted papers are those with a decision of ACCEPT.
     *
     * @return acceptance rate in [0.0, 1.0]
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

    /**
     * Gets the papers submitted by this author.
     *
     * @return the papers list
     */
    public List<Paper> getPapers() {
        return papers;
    }

    /**
     * Sets the papers submitted by this author.
     *
     * @param papers the papers list to set
     */
    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}

/**
 * Represents a reviewer who has review assignments.
 */
class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    /**
     * Creates a reviewer with an empty assignment list.
     */
    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    /**
     * Gets the review assignments for this reviewer.
     *
     * @return the assignments list
     */
    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    /**
     * Sets the review assignments for this reviewer.
     *
     * @param assignments the assignments list to set
     */
    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Adds a review assignment.
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
     * Calculates the number of unsubmitted reviews.
     * A review is unsubmitted when its grade is UNDECIDED.
     *
     * @return the number of unsubmitted reviews
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
     * Calculates the average score of submitted review grades.
     * ACCEPT is scored as 1, REJECT as 0, and UNDECIDED reviews are ignored.
     *
     * @return average score in [0.0, 1.0], or 0.0 if no submitted reviews exist
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }

        int submitted = 0;
        int scoreSum = 0;

        for (ReviewAssignment assignment : assignments) {
            if (assignment == null) {
                continue;
            }
            Grade grade = assignment.getGrade();
            if (grade == Grade.ACCEPT) {
                submitted++;
                scoreSum += 1;
            } else if (grade == Grade.REJECT) {
                submitted++;
            }
        }

        if (submitted == 0) {
            return 0.0;
        }

        return (double) scoreSum / submitted;
    }
}

/**
 * Represents a co-chair who can make final decisions on papers.
 */
class CoChair extends UserRole {

    /**
     * Creates a co-chair.
     */
    public CoChair() {
    }

    /**
     * Makes the final decision for a paper.
     *
     * @param paper    the paper to decide on
     * @param decision the final decision
     * @return 1 if the decision was applied successfully, otherwise 0
     */
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null) {
            return 0;
        }
        paper.setDecision(decision);
        return 1;
    }
}

/**
 * Type of paper.
 */
enum PaperType {
    RESEARCH,
    EXPERIENCE
}

/**
 * Review or decision grade.
 */
enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

/**
 * Represents a paper submitted to the system.
 */
class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    /**
     * Creates an empty paper.
     */
    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
    }

    /**
     * Determines whether all submitted reviews are consistent.
     * Returns true if there are at least three reviews and all non-null grades
     * are either all ACCEPT or all REJECT. UNDECIDED grades make the result false.
     *
     * @return true if all reviews are consistent and at least three exist
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }

        Grade expected = null;
        int count = 0;

        for (ReviewAssignment review : reviews) {
            if (review == null || review.getGrade() == null || review.getGrade() == Grade.UNDECIDED) {
                return false;
            }
            if (expected == null) {
                expected = review.getGrade();
            } else if (expected != review.getGrade()) {
                return false;
            }
            count++;
        }

        return count >= 3;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
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
     * Gets the final decision.
     *
     * @return the decision
     */
    public Grade getDecision() {
        return decision;
    }

    /**
     * Sets the final decision.
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
     * Gets all reviews for this paper.
     *
     * @return the reviews list
     */
    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    /**
     * Sets the reviews for this paper.
     *
     * @param reviews the reviews list to set
     */
    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
}

/**
 * Represents a review assignment with feedback and a grade.
 */
class ReviewAssignment {
    private String feedback;
    private Grade grade;

    /**
     * Creates an empty review assignment.
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