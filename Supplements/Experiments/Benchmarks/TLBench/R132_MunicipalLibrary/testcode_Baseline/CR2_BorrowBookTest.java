import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class CR2_BorrowBookTest {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        FORMAT.setLenient(false);
    }

    private Date date(String value) throws ParseException {
        return FORMAT.parse(value);
    }

    private Member findMember(Library library, String firstName, String surname) {
        for (Member member : library.getMembers()) {
            if (firstName.equals(member.getFirstName()) && surname.equals(member.getSurname())) {
                return member;
            }
        }
        return null;
    }

    private Book createBook(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        return book;
    }

    private Dictionary createDictionary(String title, String author) {
        Dictionary dictionary = new Dictionary();
        dictionary.setTitle(title);
        dictionary.setAuthor(author);
        return dictionary;
    }

    private Comic createComic(String title, String author, String recipientName) {
        Comic comic = new Comic();
        comic.setTitle(title);
        comic.setAuthor(author);
        comic.setRecipientName(recipientName);
        return comic;
    }

    private Journal createJournal(String title, Date publicationDate) {
        Journal journal = new Journal();
        journal.setTitle(title);
        journal.setPublicationDate(publicationDate);
        return journal;
    }

    @Test
    public void tc1_BorrowFirstBook() throws Exception {
        Library library = new Library();
        library.addDocument(createBook("Time Travel", "H. Wells"));
        library.addDocument(createDictionary("Latin Lexicon", "Dr Lingua"));
        library.addDocument(createComic("One Shot", "Cart Man", "Teens"));
        library.addDocument(createComic("Dual Worlds", "Art Gal", "Adults"));
        assertTrue(library.registerMember("Carl", "Nova"));
        Member member = findMember(library, "Carl", "Nova");

        boolean result = member.borrowBook("Time Travel", date("2025-06-01"));

        assertTrue(result);
        assertEquals(1, member.getBorrowRecords().size());
        assertEquals(date("2025-06-08"), member.getBorrowRecords().get(0).getReturnDue());
    }

    @Test
    public void tc2_BookAlreadyLoanedOut() throws Exception {
        Library library = new Library();
        library.addDocument(createBook("Time Travel", "H. Wells"));
        assertTrue(library.registerMember("Carl", "Nova"));
        assertTrue(library.registerMember("Dana", "Quill"));
        Member firstMember = findMember(library, "Carl", "Nova");
        Member secondMember = findMember(library, "Dana", "Quill");
        assertTrue(firstMember.borrowBook("Time Travel", date("2025-06-01")));

        boolean result = secondMember.borrowBook("Time Travel", date("2025-06-02"));

        assertFalse(result);
        assertEquals(0, secondMember.getBorrowRecords().size());
    }

    @Test
    public void tc3_MemberReachesBorrowingLimit() throws Exception {
        Library library = new Library();
        library.addDocument(createBook("Star Atlas A", "Sky View"));
        library.addDocument(createBook("Star Atlas B", "Sky View"));
        library.addDocument(createBook("Star Atlas C", "Sky View"));
        library.addDocument(createBook("Star Atlas", "Sky View"));
        assertTrue(library.registerMember("Evan", "Ray"));
        Member member = findMember(library, "Evan", "Ray");
        Date borrowedOn = date("2025-07-07");
        assertTrue(member.borrowBook("Star Atlas A", borrowedOn));
        assertTrue(member.borrowBook("Star Atlas B", borrowedOn));
        assertTrue(member.borrowBook("Star Atlas C", borrowedOn));

        boolean result = member.borrowBook("Star Atlas", date("2025-07-10"));

        assertFalse(result);
        assertEquals(3, member.getBorrowRecords().size());
    }

    @Test
    public void tc4_NonBookDocumentRejected() throws Exception {
        Library library = new Library();
        library.addDocument(createJournal("Science Weekly", date("2025-04-01")));
        library.addDocument(createBook("BIO Basics", "Bio Writer"));
        assertTrue(library.registerMember("Fiona", "Stone"));
        Member member = findMember(library, "Fiona", "Stone");
        assertTrue(member.borrowBook("BIO Basics", date("2025-01-25")));

        boolean result = member.borrowBook("Science Weekly", date("2025-04-05"));

        assertFalse(result);
        assertEquals(1, member.getBorrowRecords().size());
    }

    @Test
    public void tc5_SecondBookStillAllowed() throws Exception {
        Library library = new Library();
        library.addDocument(createBook("Deep Sea", "O. Current"));
        library.addDocument(createBook("Open Ocean", "O. Current"));
        assertTrue(library.registerMember("Gina", "Wave"));
        Member member = findMember(library, "Gina", "Wave");
        assertTrue(member.borrowBook("Deep Sea", date("2025-01-25")));
        assertTrue(member.returnBook("Deep Sea", date("2025-01-29")));

        boolean result = member.borrowBook("Deep Sea", date("2025-05-15"));

        assertTrue(result);
        assertEquals(1, member.getBorrowRecords().size());
        assertTrue(member.borrowBook("Open Ocean", date("2025-05-15")));
    }
}
