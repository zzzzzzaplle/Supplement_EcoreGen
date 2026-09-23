import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

public class CR5_ListBorrowedBookTitlesTest {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        FORMAT.setLenient(false);
    }

    private java.util.Date date(String value) throws ParseException {
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

    private BorrowRecord createBorrowRecord(java.util.Date date, Book book) {
        BorrowRecord record = new BorrowRecord();
        record.setBorrowingDate(date);
        record.setBook(book);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        record.setReturnDue(cal.getTime());
        return record;
    }

    @Test
    public void tc1_MemberWithThreeLoans() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Quinn", "Orbit");
        member.getBorrowRecords().add(createBorrowRecord(date("2025-03-01"), addBook(library, "Mars Diary", "A. Astro")));
        member.getBorrowRecords().add(createBorrowRecord(date("2025-03-01"), addBook(library, "Venus Roses", "B. Botany")));
        member.getBorrowRecords().add(createBorrowRecord(date("2025-03-01"), addBook(library, "Earth Chronicle", "C. Core")));

        List<String> borrowedTitles = member.listBorrowedBookTitles();

        assertEquals(3, borrowedTitles.size());
        assertTrue(borrowedTitles.contains("Mars Diary"));
        assertTrue(borrowedTitles.contains("Venus Roses"));
        assertTrue(borrowedTitles.contains("Earth Chronicle"));
    }

    @Test
    public void tc2_MemberWithNoLoans() {
        Library library = new Library();
        Member member = registerMember(library, "Rita", "Null");

        List<String> borrowedTitles = member.listBorrowedBookTitles();

        assertTrue(borrowedTitles.isEmpty());
    }

    @Test
    public void tc3_OneReturnedOneActive() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Sam", "Toggle");
        Book cityLights = addBook(library, "City Lights", "D. Urban");
        Book moonOrbit = addBook(library, "Moon Orbit", "E. Lunar");
        member.getBorrowRecords().add(createBorrowRecord(date("2025-02-01"), cityLights));
        assertTrue(member.returnBook("City Lights", date("2025-02-05")));
        member.getBorrowRecords().add(createBorrowRecord(date("2025-02-28"), moonOrbit));

        List<String> borrowedTitles = member.listBorrowedBookTitles();

        assertEquals(1, borrowedTitles.size());
        assertEquals("Moon Orbit", borrowedTitles.get(0));
    }

    @Test
    public void tc4_MemberAtLimit() throws Exception {
        Library library = new Library();
        Member member = registerMember(library, "Tara", "Limit");
        member.getBorrowRecords().add(createBorrowRecord(date("2025-04-10"), addBook(library, "Alpha Zone", "F. Frontier")));
        member.getBorrowRecords().add(createBorrowRecord(date("2025-04-11"), addBook(library, "Beta Drift", "G. Galaxy")));
        member.getBorrowRecords().add(createBorrowRecord(date("2025-04-12"), addBook(library, "Gamma Ray", "H. Horizon")));

        List<String> borrowedTitles = member.listBorrowedBookTitles();

        assertEquals(3, borrowedTitles.size());
        assertTrue(borrowedTitles.contains("Alpha Zone"));
        assertTrue(borrowedTitles.contains("Beta Drift"));
        assertTrue(borrowedTitles.contains("Gamma Ray"));
    }

    @Test
    public void tc5_BrandNewMember() {
        Library library = new Library();
        Member member = registerMember(library, "Uma", "Fresh");

        List<String> borrowedTitles = member.listBorrowedBookTitles();

        assertTrue(borrowedTitles.isEmpty());
    }
}
