import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.conference.Author;
import edu.conference.ConferenceFactory;
import edu.conference.Grade;
import edu.conference.Paper;
import edu.conference.PaperType;

public class CR4Test {
    private static final double DELTA = 0.001;
    private final ConferenceFactory factory = ConferenceFactory.eINSTANCE;

    @Test
    public void perfectAcceptanceRate() {
        Author author = createAuthor();
        submitPaper(author, "P30", Grade.ACCEPT);
        submitPaper(author, "P31", Grade.ACCEPT);
        submitPaper(author, "P32", Grade.ACCEPT);

        assertEquals(1.00, author.calculateAcceptanceRate(), DELTA);
    }

    @Test
    public void fiftyPercentAcceptanceRate() {
        Author author = createAuthor();
        submitPaper(author, "P33", Grade.ACCEPT);
        submitPaper(author, "P34", Grade.REJECT);

        assertEquals(0.50, author.calculateAcceptanceRate(), DELTA);
    }

    @Test
    public void noAcceptedPapers() {
        Author author = createAuthor();
        submitPaper(author, "P35", Grade.REJECT);
        submitPaper(author, "P36", Grade.REJECT);
        submitPaper(author, "P37", Grade.REJECT);

        assertEquals(0.00, author.calculateAcceptanceRate(), DELTA);
    }

    @Test
    public void mixedDecisionsWithOneAcceptance() {
        Author author = createAuthor();
        submitPaper(author, "P38", Grade.ACCEPT);
        submitPaper(author, "P39", Grade.REJECT);
        submitPaper(author, "P40", Grade.REJECT);

        assertEquals(0.33, author.calculateAcceptanceRate(), 0.01);
    }

    @Test
    public void singlePaperAuthor() {
        Author author = createAuthor();
        submitPaper(author, "P41", Grade.ACCEPT);

        assertEquals(1.00, author.calculateAcceptanceRate(), DELTA);
    }

    @Test
    public void authorWithNoPapers() {
        Author author = createAuthor();

        assertEquals(0.00, author.calculateAcceptanceRate(), DELTA);
    }

    private Author createAuthor() {
        return factory.createAuthor();
    }

    private void submitPaper(Author author, String title, Grade decision) {
        Paper paper = factory.createPaper();
        paper.setTitle(title);
        paper.setType(PaperType.RESEARCH);
        paper.setDecision(decision);
        author.submitPaper(paper);
    }
}
