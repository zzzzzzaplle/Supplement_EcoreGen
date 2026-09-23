import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class CR4_ReturnBookTest {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        FORMAT.setLenient(false);
    }

    private Date date(String value) throws ParseException {
        return FORMAT.parse(value);
    }

    private Member registerMember(Library library, String firstName, String surname) {
        assertTrue(library.registerMember(firstName, surname));
        for (Member member : library.getMembers()) {
            if (firstName.equals(member.getFirstName()) && surname.equals(member.getSurname())) {
                return member;
            }
        }
        return null;
    }

    private Book addBook(Library library, String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        library.addDocument(book);
        return book;
    }

    @Test
    public void tc1_ReturnOwnedBook() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Mike", "Stream");
        Book book = addBook(library, "Ocean Currents", "Dr Blue");
        BorrowRecord record = new BorrowRecord(date("2025-06-25"), book);
        record.setReturnDue(date("2025-07-09"));
        member.getBorrowRecords().add(record);

        boolean result = member.returnBook("Ocean Currents", date("2025-07-08"));

        assertTrue(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void tc2_ReturnNonBorrowedTitle() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Mike", "Stream");
        addBook(library, "Mountain Peaks", "Alp Climb");

        boolean result = member.returnBook("Mountain Peaks", date("2025-07-10"));

        assertFalse(result);
    }

    @Test
    public void tc3_ReturnAfterDueDatePassed() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Nora", "Sand");
        Book book = addBook(library, "Desert Storm", "Dune Wind");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-07-25"), book));

        boolean result = member.returnBook("Desert Storm", date("2025-08-05"));

        assertFalse(result);
        assertEquals(1, member.getBorrowRecords().size());
    }

    @Test
    public void tc4_ReturnWhenNoActiveLoans() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Owen", "Plot");
        addBook(library, "Hidden Garden", "Leaf Light");

        boolean result = member.returnBook("Hidden Garden", date("2025-07-10"));

        assertFalse(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void tc5_ReturnLastRemainingBook() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Paula", "Frost");
        Book book = addBook(library, "Polar Night", "Arctic Star");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-08-10"), book));

        boolean result = member.returnBook("Polar Night", date("2025-08-11"));

        assertTrue(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }
}
