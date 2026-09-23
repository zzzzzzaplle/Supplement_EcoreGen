import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class CR3_ExtendDueDateTest {
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
    public void tc1_EligibleForExtension() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Hugh", "Bright");
        Book book = addBook(library, "Solar Physics", "Dr Ray");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-01-01"), book));

        Date extendedDate = member.extendReturnDueDate("Solar Physics", date("2025-01-05"));

        assertEquals(date("2025-01-15"), extendedDate);
    }

    @Test
    public void tc2_TodayEqualsDueDate() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Ian", "Saga");
        Book book = addBook(library, "Ancient Myths", "Hist Lore");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-02-03"), book));

        Date extendedDate = member.extendReturnDueDate("Ancient Myths", date("2025-02-10"));

        assertEquals(date("2025-02-10"), extendedDate);
    }

    @Test
    public void tc3_TodayBeforeBorrowingDate() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Jill", "Matrix");
        Book book = addBook(library, "Linear Algebra", "Prof Calc");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-03-05"), book));

        Date extendedDate = member.extendReturnDueDate("Linear Algebra", date("2025-03-03"));

        assertEquals(date("2025-03-12"), extendedDate);
    }

    @Test
    public void tc4_MultipleBorrowingByMember() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Karl", "Paint");
        Book modernArt = addBook(library, "Modern Art", "Lores");
        Book colorTheory = addBook(library, "Color Theory", "Lores");
        member.getBorrowRecords().add(new BorrowRecord(date("2025-04-01"), modernArt));
        member.getBorrowRecords().add(new BorrowRecord(date("2025-04-02"), colorTheory));

        Date extendedDate = member.extendReturnDueDate("Modern Art", date("2025-04-05"));

        assertEquals(date("2025-04-15"), extendedDate);
        assertEquals(2, member.getBorrowRecords().size());
    }

    @Test
    public void tc5_SecondExtensionAttempt() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Lara", "Wave");
        Book book = addBook(library, "Quantum Fields", "Phys Mind");
        BorrowRecord record = new BorrowRecord(date("2025-05-01"), book);
        record.setReturnDue(date("2025-05-22"));
        member.setBorrowRecords(new ArrayList<BorrowRecord>());
        member.getBorrowRecords().add(record);

        Date extendedDate = member.extendReturnDueDate("Quantum Fields", date("2025-05-18"));

        assertEquals(date("2025-05-29"), extendedDate);
    }
}
