// ==version1==
```
class User {
    - String name
    - List<UserRole> roles

    //getter,setter
    + String getName()
    + void setName(String name)
    + List<UserRole> getRoles()
    + void addRole(UserRole role)
}

abstract class UserRole {
    - String id

    //getter,setter
    + String getId()
    + void setId(String id)
}

class Author extends UserRole {
    - List<Paper> papers

     // key operations
    + void submitPaper(Paper paper)
    + int countSubmittedPapers()
    + double calculateAcceptanceRate()

    //getter,setter
    + List<Paper> getPapers()
    
}

class Reviewer extends UserRole {
    - List<ReviewAssignment> assignments

    //getter,setter
    + List<ReviewAssignment> getReviewAssignments()
    + void addReviewAssignment(ReviewAssignment assignment)

    //key operations
    + int calculateUnsubmittedReviews()
    + double calculateSubmittedReviewAverageScore()
}

class CoChair extends UserRole {
    + int makeFinalDecision(Paper paper, Grade decision)
}

User *-- "*" UserRole

enum PaperType {
    RESEARCH
    EXPERIENCE
}

enum Grade {
    UNDECIDED
    ACCEPT
    REJECT
}

class Paper {
    - String title
    - PaperType type
    - Grade decision
    - List<ReviewAssignment> reviews

    //key operations
    + boolean isAllReviewsConsistent()

    //getter,setter
    + String getTitle()
    + void setTitle(String title)
    + PaperType getType()
    + void setType(PaperType type)
    + Grade getDecision()
    + void setDecision(Grade decision)
    + void addReview(ReviewAssignment review)
    + List<ReviewAssignment> getReviews()

}

class ReviewAssignment {
    - String feedback
    - Grade grade

    //getter,setter
    + String getFeedback()
    + Grade getGrade()
    + void setFeedback(String feedback)
    + void setGrade(Grade grade)
}

Author "1" -- "*" Paper : papers
Reviewer "1" -- "*" ReviewAssignment : assignments
Paper "1" -- "*" ReviewAssignment : reviews
```
// ==end==
