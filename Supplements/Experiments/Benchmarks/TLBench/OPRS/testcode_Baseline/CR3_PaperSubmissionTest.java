
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class CR3_PaperSubmissionTest {
    public Paper createPaper(String id, PaperType type) {
        Paper paper = new Paper();
        paper.setTitle(id);
        paper.setType(type);
        return paper;
    }

    @org.junit.Test
    public void tc1_AuthorWithFivePapers() {
        // Test logic (implement test case 1 set up)

        User authorA001 = new User();
        authorA001.setName("A001");
        Author author = new Author();
        authorA001.addRole(author);

        for (int i = 0; i < 5; i++) {
            author.getPapers().add(createPaper("P" + (19 + i), PaperType.RESEARCH));
        }

        // Assertions
        assertEquals("Paper count mismatch for Author A001", 5, author.countSubmittedPapers());
    }

    @org.junit.Test
    public void tc2_NewAuthorWithZeroPapers() {
        // Test logic (implement test case 2 set up)
        User authorA002 = new User();
        authorA002.setName("A002");
        Author author = new Author();
        authorA002.addRole(author);

        // Assertions
        assertEquals("Paper count mismatch for Author A002", 0, author.countSubmittedPapers());
    }

    @org.junit.Test
    public void tc3_SinglePaperAuthor() {
        // Test logic (implement test case 3 set up)
        User authorA003 = new User();
        authorA003.setName("A003");
        Author author = new Author();
        authorA003.addRole(author);

        Paper paper = createPaper("P24", PaperType.RESEARCH);
        author.submitPaper(paper); // Create single paper linked to A003

        // Assertions
        assertEquals("Paper count mismatch for Author A003", 1, author.countSubmittedPapers());
    }

    @org.junit.Test
    public void tc4_MultiRoleUser() {
        // Test logic (implement test case 4 set up)
        User authorA004 = new User();
        authorA004.setName("A004");
        Author author = new Author();
        authorA004.addRole(author);

        Paper paper1 = createPaper("P25", PaperType.RESEARCH);
        Paper paper2 = createPaper("P26", PaperType.EXPERIENCE);
        Paper paper3 = createPaper("P27", PaperType.RESEARCH);
        author.submitPaper(paper1);
        author.submitPaper(paper2);
        author.submitPaper(paper3); // User U1 with Author and Reviewer roles

        // Assertions
        assertEquals("Paper count mismatch for Author A004", 3, author.countSubmittedPapers());
    }

    @org.junit.Test
    public void tc5_PaperOwnershipValidation() {
        // Test logic (implement test case 5 set up)
        User authorA005 = new User();
        authorA005.setName("A005");
        Author author5 = new Author();
        authorA005.addRole(author5);

        Paper paperA005 = createPaper("P28", PaperType.RESEARCH); // Paper linked to A005
        author5.submitPaper(paperA005);

        User authorA006 = new User();
        authorA006.setName("A006");
        Author author6 = new Author();
        authorA006.addRole(author6);

        Paper paperA006 = createPaper("P29", PaperType.RESEARCH); // Paper linked to A006
        author6.submitPaper(paperA006);

        // Assertions
        assertEquals("Paper count mismatch for Author A005", 1, author5.countSubmittedPapers());
    }

    private List<Paper> createPapers(int count) {
        List<Paper> papers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            papers.add(createPaper("P" + (19 + i), PaperType.RESEARCH));
        }
        return papers;
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
 * Time: 0.025
 * 
 * OK (5 tests)
 * 
 * 
 */