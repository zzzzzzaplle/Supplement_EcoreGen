public class TestSystem {
    public static void main(String[] args) {
        // Test 1: Calculate unsubmitted reviews
        Reviewer reviewer = new Reviewer();
        ReviewAssignment r1 = new ReviewAssignment();
        r1.setGrade(Grade.ACCEPT);
        reviewer.addReviewAssignment(r1);
        
        ReviewAssignment r2 = new ReviewAssignment();
        r2.setGrade(Grade.UNDECIDED);
        reviewer.addReviewAssignment(r2);
        
        System.out.println("Unsubmitted reviews: " + reviewer.calculateUnsubmittedReviews());
        // Expected: 1
        
        // Test 2: All reviews consistent
        Paper paper = new Paper();
        paper.setTitle("Test Paper");
        
        ReviewAssignment ra1 = new ReviewAssignment();
        ra1.setGrade(Grade.ACCEPT);
        ReviewAssignment ra2 = new ReviewAssignment();
        ra2.setGrade(Grade.ACCEPT);
        ReviewAssignment ra3 = new ReviewAssignment();
        ra3.setGrade(Grade.ACCEPT);
        
        paper.addReview(ra1);
        paper.addReview(ra2);
        paper.addReview(ra3);
        
        System.out.println("All reviews consistent (all ACCEPT): " + paper.isAllReviewsConsistent());
        // Expected: true
        
        // Test with mixed
        Paper paper2 = new Paper();
        paper2.setTitle("Mixed Paper");
        ReviewAssignment rb1 = new ReviewAssignment();
        rb1.setGrade(Grade.ACCEPT);
        ReviewAssignment rb2 = new ReviewAssignment();
        rb2.setGrade(Grade.REJECT);
        ReviewAssignment rb3 = new ReviewAssignment();
        rb3.setGrade(Grade.ACCEPT);
        paper2.addReview(rb1);
        paper2.addReview(rb2);
        paper2.addReview(rb3);
        System.out.println("All reviews consistent (mixed): " + paper2.isAllReviewsConsistent());
        // Expected: false
        
        // Test 3: Count submitted papers
        Author author = new Author();
        Paper p1 = new Paper();
        p1.setTitle("Paper 1");
        Paper p2 = new Paper();
        p2.setTitle("Paper 2");
        author.submitPaper(p1);
        author.submitPaper(p2);
        System.out.println("Total papers: " + author.countSubmittedPapers());
        // Expected: 2
        
        // Test 4: Acceptance rate
        p1.setDecision(Grade.ACCEPT);
        p2.setDecision(Grade.REJECT);
        System.out.println("Acceptance rate: " + String.format("%.2f", author.calculateAcceptanceRate()));
        // Expected: 0.50
        
        // Test 5: Average score
        Reviewer reviewer2 = new Reviewer();
        ReviewAssignment rx1 = new ReviewAssignment();
        rx1.setGrade(Grade.ACCEPT);
        ReviewAssignment rx2 = new ReviewAssignment();
        rx2.setGrade(Grade.ACCEPT);
        ReviewAssignment rx3 = new ReviewAssignment();
        rx3.setGrade(Grade.ACCEPT);
        ReviewAssignment rx4 = new ReviewAssignment();
        rx4.setGrade(Grade.REJECT);
        reviewer2.addReviewAssignment(rx1);
        reviewer2.addReviewAssignment(rx2);
        reviewer2.addReviewAssignment(rx3);
        reviewer2.addReviewAssignment(rx4);
        System.out.println("Average score: " + String.format("%.3f", reviewer2.calculateSubmittedReviewAverageScore()));
        // Expected: 0.750
    }
}
