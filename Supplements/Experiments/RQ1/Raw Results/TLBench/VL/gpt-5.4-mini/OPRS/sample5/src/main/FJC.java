import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a system user who may have one or more roles.
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
     * Gets the user name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user name.
     *
     * @param name user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the roles assigned to this user.
     *
     * @return list of roles
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Adds a role to this user.
     *
     * @param role role to add
     */
    public void addRole(UserRole role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (role != null) {
            roles.add(role);
        }
    }
}

/**
 * Base class for all user roles.
 */
abstract class UserRole {
    private String id;

    /**
     * Creates an empty role.
     */
    public UserRole() {
    }

    /**
     * Gets the role identifier.
     *
     * @return role id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the role identifier.
     *
     * @param id role id
     */
    public void setId(String id) {
        this.id = id;
    }
}

/**
 * An author role that can submit papers.
 */
class Author extends UserRole {
    private List<Paper> papers;

    /**
     * Creates an empty author.
     */
    public Author() {
        this.papers = new ArrayList<>();
    }

    /**
     * Submits a paper.
     *
     * @param paper paper to submit
     */
    public void submitPaper(Paper paper) {
        if (papers == null) {
            papers = new ArrayList<>();
        }
        if (paper != null) {
            papers.add(paper);
        }
    }

    /**
     * Counts the total number of papers submitted by this author.
     *
     * @return number of submitted papers
     */
    public int countSubmittedPapers() {
        return papers == null ? 0 : papers.size();
    }

    /**
     * Calculates the acceptance rate of papers for this author.
     * Accepted papers are those with final decision ACCEPT.
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
     * @return list of papers
     */
    public List<Paper> getPapers() {
        return papers;
    }

    /**
     * Sets the papers submitted by this author.
     *
     * @param papers list of papers
     */
    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}

/**
 * A reviewer role that handles review assignments.
 */
class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    /**
     * Creates an empty reviewer.
     */
    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    /**
     * Gets the review assignments of this reviewer.
     *
     * @return list of review assignments
     */
    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    /**
     * Sets the review assignments of this reviewer.
     *
     * @param assignments list of assignments
     */
    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Adds a review assignment to this reviewer.
     *
     * @param assignment assignment to add
     */
    public void addReviewAssignment(ReviewAssignment assignment) {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        if (assignment != null) {
            assignments.add(assignment);
        }
    }

    /**
     * Calculates the number of unsubmitted reviews for this reviewer.
     * A review is considered unsubmitted when its grade is UNDECIDED.
     *
     * @return count of unsubmitted reviews
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
     * ACCEPT = 1, REJECT = 0, UNDECIDED reviews are ignored.
     *
     * @return average score in [0.0, 1.0], or 0.0 if no submitted reviews
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int submittedCount = 0;
        int scoreSum = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment == null) {
                continue;
            }
            Grade grade = assignment.getGrade();
            if (grade == Grade.ACCEPT) {
                submittedCount++;
                scoreSum += 1;
            } else if (grade == Grade.REJECT) {
                submittedCount++;
                scoreSum += 0;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) scoreSum / submittedCount;
    }
}

/**
 * A co-chair role that can make final decisions on papers.
 */
class CoChair extends UserRole {

    /**
     * Creates an empty co-chair.
     */
    public CoChair() {
    }

    /**
     * Makes the final decision for a paper.
     *
     * @param paper paper to decide on
     * @param decision final decision
     * @return 0 if decision applied successfully, -1 otherwise
     */
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null || decision == null) {
            return -1;
        }
        paper.setDecision(decision);
        return 0;
    }
}

/**
 * Paper type classification.
 */
enum PaperType {
    RESEARCH,
    EXPERIENCE
}

/**
 * Review grade / decision status.
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
     * Checks whether all reviews are consistent.
     * Consistent means there are at least three reviews and all submitted
     * grades are exclusively ACCEPT or exclusively REJECT.
     *
     * @return true if consistent, false otherwise
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        Grade firstSubmitted = null;
        int submittedCount = 0;
        for (ReviewAssignment review : reviews) {
            if (review == null) {
                continue;
            }
            Grade grade = review.getGrade();
            if (grade == Grade.UNDECIDED || grade == null) {
                continue;
            }
            submittedCount++;
            if (firstSubmitted == null) {
                firstSubmitted = grade;
            } else if (firstSubmitted != grade) {
                return false;
            }
        }
        return submittedCount >= 3 && firstSubmitted != null;
    }

    /**
     * Gets the paper title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the paper title.
     *
     * @param title paper title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the paper type.
     *
     * @return paper type
     */
    public PaperType getType() {
        return type;
    }

    /**
     * Sets the paper type.
     *
     * @param type paper type
     */
    public void setType(PaperType type) {
        this.type = type;
    }

    /**
     * Gets the final decision for this paper.
     *
     * @return decision grade
     */
    public Grade getDecision() {
        return decision;
    }

    /**
     * Sets the final decision for this paper.
     *
     * @param decision final decision
     */
    public void setDecision(Grade decision) {
        this.decision = decision;
    }

    /**
     * Adds a review to this paper.
     *
     * @param review review to add
     */
    public void addReview(ReviewAssignment review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        if (review != null) {
            reviews.add(review);
        }
    }

    /**
     * Gets the reviews for this paper.
     *
     * @return list of reviews
     */
    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    /**
     * Sets the reviews for this paper.
     *
     * @param reviews list of reviews
     */
    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }
}

/**
 * Represents a review assignment for a paper.
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
     * Gets the feedback text.
     *
     * @return feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the feedback text.
     *
     * @param feedback feedback text
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Gets the review grade.
     *
     * @return grade
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Sets the review grade.
     *
     * @param grade grade to set
     */
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}