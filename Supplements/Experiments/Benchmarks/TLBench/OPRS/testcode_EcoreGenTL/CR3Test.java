import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.conference.Author;
import edu.conference.ConferenceFactory;
import edu.conference.Paper;
import edu.conference.PaperType;
import edu.conference.Reviewer;
import edu.conference.User;

public class CR3Test {
    private final ConferenceFactory factory = ConferenceFactory.eINSTANCE;

    @Test
    public void authorWithFivePapers() {
        Author author = createAuthor();
        submitPapers(author, "P19", "P20", "P21", "P22", "P23");

        assertEquals(5, author.countSubmittedPapers());
    }

    @Test
    public void newAuthorWithZeroPapers() {
        Author author = createAuthor();

        assertEquals(0, author.countSubmittedPapers());
    }

    @Test
    public void singlePaperAuthor() {
        Author author = createAuthor();
        submitPapers(author, "P24");

        assertEquals(1, author.countSubmittedPapers());
    }

    @Test
    public void multiRoleUserAuthorReviewer() {
        User user = factory.createUser();
        Author author = createAuthor();
        Reviewer reviewer = factory.createReviewer();
        user.getRoles().add(author);
        user.getRoles().add(reviewer);
        submitPapers(author, "P25", "P26", "P27");

        assertEquals(3, author.countSubmittedPapers());
    }

    @Test
    public void paperOwnershipValidation() {
        Author authorA005 = createAuthor();
        Author authorA006 = createAuthor();
        submitPapers(authorA005, "P28");
        submitPapers(authorA006, "P29");

        assertEquals(1, authorA005.countSubmittedPapers());
    }

    private Author createAuthor() {
        return factory.createAuthor();
    }

    private void submitPapers(Author author, String... titles) {
        for (String title : titles) {
            author.submitPaper(createPaper(title));
        }
    }

    private Paper createPaper(String title) {
        Paper paper = factory.createPaper();
        paper.setTitle(title);
        paper.setType(PaperType.RESEARCH);
        return paper;
    }
}
