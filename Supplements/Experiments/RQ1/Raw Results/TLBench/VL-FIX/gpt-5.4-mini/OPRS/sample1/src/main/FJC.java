import java.util.ArrayList;
import java.util.List;

/**
 * Represents a system user.
 */
class User {
    private String name;
    private List<UserRole> roles;

    /**
     * Creates an empty User.
     */
    public User() {
        this.roles = new ArrayList<>();
    }

    /**
     * Creates a User with the specified name.
     *
     * @param name the user's name
     */
    public User(String name) {
        this();
        this.name = name;
    }

    /**
     * Returns the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's roles.
     *
     * @return the list of roles
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Sets the user's roles.
     *
     * @param roles the list of roles
     */
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * Adds a role to the user.
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
 * Abstract base class for all user roles.
 */
abstract class UserRole {
    private String id;

    /**
     * Creates a UserRole with no id.
     */
    public UserRole() {
    }

    /**
     * Creates a UserRole with the specified id.
     *
     * @param id the role id
     */
    public UserRole(String id) {
        this.id = id;
    }

    /**
     * Returns the role id.
     *
     * @return the role id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the role id.
     *
     * @param id the role id
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
     * Creates an Author with no papers.
     */
    public Author() {
        this.papers = new ArrayList<>();
    }

    /**
     * Creates an Author with the specified id.
     *
     * @param id the role id
     */
    public Author(String id) {
        super(id);
        this.papers = new ArrayList<>();
    }

    /**
     * Returns the papers submitted by this author.
     *
     * @return the list of papers
     */
    public List<Paper> getPapers() {
        return papers;
    }

    /**
     * Sets the papers submitted by this author.
     *
     * @param papers the list of papers
     */
    public void setPapers(List<Paper> papers) {
        this.papers = papers;
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
     * Counts the number of submitted papers.
     *
     * @return the number of papers
     */
    public int countSubmittedPapers() {
        return papers == null ? 0 : papers.size();
    }

    /**
     * Calculates the acceptance rate of this author's papers.
     *
     * @return acceptance rate in [0.0, 1.0], or 0.0 if no papers were submitted
     */
    public double calculateAcceptanceRate() {
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        int total = papers.size();
        for (Paper paper : papers) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / total;
    }
}

/**
 * Represents a reviewer role.
 */
class Reviewer extends UserRole {
    private List<ReviewAssignment> assignments;

    /**
     * Creates a Reviewer with no assignments.
     */
    public Reviewer() {
        this.assignments = new ArrayList<>();
    }

    /**
     * Creates a Reviewer with the specified id.
     *
     * @param id the role id
     */
    public Reviewer(String id) {
        super(id);
        this.assignments = new ArrayList<>();
    }

    /**
     * Returns the review assignments for this reviewer.
     *
     * @return the list of review assignments
     */
    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    /**
     * Sets the review assignments for this reviewer.
     *
     * @param assignments the list of review assignments
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
     * ACCEPT counts as 1, REJECT counts as 0, and UNDECIDED is ignored.
     *
     * @return average score in [0.0, 1.0], or 0.0 if there are no submitted reviews
     */
    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null || assignments.isEmpty()) {
            return 0.0;
        }
        int submittedCount = 0;
        int sum = 0;
        for (ReviewAssignment assignment : assignments) {
            if (assignment == null || assignment.getGrade() == null) {
                continue;
            }
            if (assignment.getGrade() == Grade.ACCEPT) {
                sum += 1;
                submittedCount++;
            } else if (assignment.getGrade() == Grade.REJECT) {
                submittedCount++;
            }
        }
        if (submittedCount == 0) {
            return 0.0;
        }
        return (double) sum / submittedCount;
    }
}

/**
 * Represents a co-chair role.
 */
class CoChair extends UserRole {
    /**
     * Creates a CoChair with no id.
     */
    public CoChair() {
        super();
    }

    /**
     * Creates a CoChair with the specified id.
     *
     * @param id the role id
     */
    public CoChair(String id) {
        super(id);
    }

    /**
     * Makes the final decision for a paper.
     *
     * @param paper the paper
     * @param decision the final decision
     * @return 1 if accepted, 0 otherwise
     */
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper != null) {
            paper.setDecision(decision);
        }
        return decision == Grade.ACCEPT ? 1 : 0;
    }
}

/**
 * Enumeration of paper types.
 */
enum PaperType {
    RESEARCH,
    EXPERIENCE
}

/**
 * Enumeration of review grades and paper decisions.
 */
enum Grade {
    UNDECIDED,
    ACCEPT,
    REJECT
}

/**
 * Represents a paper submitted by an author.
 */
class Paper {
    private String title;
    private PaperType type;
    private Grade decision;
    private List<ReviewAssignment> reviews;

    /**
     * Creates a Paper with no values.
     */
    public Paper() {
        this.reviews = new ArrayList<>();
        this.decision = Grade.UNDECIDED;
    }

    /**
     * Creates a Paper with the specified title and type.
     *
     * @param title the paper title
     * @param type the paper type
     */
    public Paper(String title, PaperType type) {
        this();
        this.title = title;
        this.type = type;
    }

    /**
     * Returns the title.
     *
     * @return the paper title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the paper title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the paper type.
     *
     * @return the paper type
     */
    public PaperType getType() {
        return type;
    }

    /**
     * Sets the paper type.
     *
     * @param type the paper type
     */
    public void setType(PaperType type) {
        this.type = type;
    }

    /**
     * Returns the final decision.
     *
     * @return the decision
     */
    public Grade getDecision() {
        return decision;
    }

    /**
     * Sets the final decision.
     *
     * @param decision the decision
     */
    public void setDecision(Grade decision) {
        this.decision = decision;
    }

    /**
     * Adds a review assignment to this paper.
     *
     * @param review the review assignment
     */
    public void addReview(ReviewAssignment review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }

    /**
     * Returns the review assignments for this paper.
     *
     * @return the list of review assignments
     */
    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    /**
     * Sets the review assignments for this paper.
     *
     * @param reviews the list of review assignments
     */
    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }

    /**
     * Checks whether all reviews for this paper are consistent.
     * Consistent means at least three submitted reviews and all submitted reviews
     * are exclusively ACCEPT or exclusively REJECT.
     *
     * @return true if consistent, false otherwise
     */
    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.size() < 3) {
            return false;
        }
        int submitted = 0;
        Grade firstSubmittedGrade = null;
        for (ReviewAssignment review : reviews) {
            if (review == null || review.getGrade() == null || review.getGrade() == Grade.UNDECIDED) {
                continue;
            }
            submitted++;
            if (firstSubmittedGrade == null) {
                firstSubmittedGrade = review.getGrade();
            } else if (firstSubmittedGrade != review.getGrade()) {
                return false;
            }
        }
        return submitted >= 3;
    }
}

/**
 * Represents a review assignment.
 */
class ReviewAssignment {
    private String feedback;
    private Grade grade;

    /**
     * Creates a ReviewAssignment with no values.
     */
    public ReviewAssignment() {
        this.grade = Grade.UNDECIDED;
    }

    /**
     * Creates a ReviewAssignment with the specified feedback and grade.
     *
     * @param feedback the review feedback
     * @param grade the review grade
     */
    public ReviewAssignment(String feedback, Grade grade) {
        this.feedback = feedback;
        this.grade = grade;
    }

    /**
     * Returns the feedback.
     *
     * @return the feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets the feedback.
     *
     * @param feedback the feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Returns the grade.
     *
     * @return the grade
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Sets the grade.
     *
     * @param grade the grade
     */
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}