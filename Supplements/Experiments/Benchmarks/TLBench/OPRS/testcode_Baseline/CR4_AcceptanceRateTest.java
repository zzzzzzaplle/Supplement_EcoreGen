
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class CR4_AcceptanceRateTest {
    public Paper createPaper(String id, PaperType type) {
        Paper paper = new Paper();
        paper.setTitle(id);
        paper.setType(type);
        return paper;
    }

    @org.junit.Test
    public void tc1_PerfectAcceptanceRate() {
        User authorA006 = new User();
        authorA006.setName("A006");
        Author author = new Author();
        authorA006.addRole(author);

        Paper paper1 = createPaper("P30", PaperType.RESEARCH);
        paper1.setDecision(Grade.ACCEPT);
        Paper paper2 = createPaper("P31", PaperType.RESEARCH);
        paper2.setDecision(Grade.ACCEPT);
        Paper paper3 = createPaper("P32", PaperType.RESEARCH);
        paper3.setDecision(Grade.ACCEPT);

        author.submitPaper(paper1);
        author.submitPaper(paper2);
        author.submitPaper(paper3);

        // Assertions
        assertEquals("Acceptance rate should be 1.00", 1.00, author.calculateAcceptanceRate(), 0.01);
    }

    // Test Case 2: 50% acceptance rate
    @org.junit.Test
    public void tc2_50PercentAcceptanceRate() {
        User authorA007 = new User();
        authorA007.setName("A007");
        Author author = new Author();
        authorA007.addRole(author);

        Paper paper1 = createPaper("P33", PaperType.RESEARCH);
        paper1.setDecision(Grade.ACCEPT);
        Paper paper2 = createPaper("P34", PaperType.RESEARCH);
        paper2.setDecision(Grade.REJECT);

        author.submitPaper(paper1);
        author.submitPaper(paper2);

        // Assertions
        assertEquals("Acceptance rate should be 0.50", 0.50, author.calculateAcceptanceRate(), 0.01);
    }

    // Test Case 3: No accepted papers
    @org.junit.Test
    public void tc3_NoAcceptedPapers() {
        User authorA008 = new User();
        authorA008.setName("A008");
        Author author = new Author();
        authorA008.addRole(author);

        Paper paper1 = createPaper("P35", PaperType.RESEARCH);
        paper1.setDecision(Grade.REJECT);
        Paper paper2 = createPaper("P36", PaperType.RESEARCH);
        paper2.setDecision(Grade.REJECT);
        Paper paper3 = createPaper("P37", PaperType.RESEARCH);
        paper3.setDecision(Grade.REJECT);

        author.submitPaper(paper1);
        author.submitPaper(paper2);
        author.submitPaper(paper3);

        // Assertions
        assertEquals("Acceptance rate should be 0.00", 0.00, author.calculateAcceptanceRate(), 0.01);
    }

    // Test Case 4: Mixed decisions with 1 acceptance
    @org.junit.Test
    public void tc4_MixedDecisionsWithOneAcceptance() {
        User authorA009 = new User();
        authorA009.setName("A009");
        Author author = new Author();
        authorA009.addRole(author);

        Paper paper1 = createPaper("P38", PaperType.RESEARCH);
        paper1.setDecision(Grade.ACCEPT);
        Paper paper2 = createPaper("P39", PaperType.RESEARCH);
        paper2.setDecision(Grade.REJECT);
        Paper paper3 = createPaper("P40", PaperType.RESEARCH);
        paper3.setDecision(Grade.REJECT);

        author.submitPaper(paper1);
        author.submitPaper(paper2);
        author.submitPaper(paper3);

        // Assertions
        assertEquals("Acceptance rate should be 0.33", 0.33, author.calculateAcceptanceRate(), 0.01);
    }

    // Test Case 5: Single paper author
    @org.junit.Test
    public void tc5_SinglePaperAuthor() {
        User authorA010 = new User();
        authorA010.setName("A010");
        Author author = new Author();
        authorA010.addRole(author);

        Paper paper1 = createPaper("P41", PaperType.RESEARCH);
        paper1.setDecision(Grade.ACCEPT);

        author.submitPaper(paper1);

        // Assertions
        assertEquals("Acceptance rate should be 1.00", 1.00, author.calculateAcceptanceRate(), 0.01);
    }
    @org.junit.Test
    public void tc6_AuthorWithNoPapers_AcceptanceRateIsZero() {
        // Test Case 6: Author with no papers
        Author author = new Author();

        double acceptanceRate = author.calculateAcceptanceRate();

        assertEquals("Acceptance rate for author with no papers should be 0.00",
                0.00, acceptanceRate, 0.0001);
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.02
 * 
 * OK (5 tests)
 * 
 * 
 */