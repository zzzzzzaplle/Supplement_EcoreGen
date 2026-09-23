package edu.municipalLibrary.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.municipalLibrary.Book;
import edu.municipalLibrary.BorrowRecord;
import edu.municipalLibrary.Library;
import edu.municipalLibrary.Member;
import edu.municipalLibrary.MunicipalLibraryFactory;

public class CR4Test {

    private MunicipalLibraryFactory factory;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        factory = MunicipalLibraryFactory.eINSTANCE;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
    }

    private Date date(String text) {
        try {
            return dateFormat.parse(text);
        } catch (ParseException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private Library createLibrary(String name) {
        Library library = factory.createLibrary();
        library.setName(name);
        return library;
    }

    private Member createMember(Library library, String firstName, String surname) {
        Member member = factory.createMember();
        member.setFirstName(firstName);
        member.setSurname(surname);
        member.setLibrary(library);
        return member;
    }

    private Book createBook(Library library, String title, String author, String bookId) {
        Book book = factory.createBook();
        book.setTitle(title);
        book.setAuthor(author);
        book.setBookId(bookId);
        library.addDocument(book);
        return book;
    }

    private BorrowRecord createBorrowRecord(Member member, Book book, String borrowingDate, String returnDue) {
        BorrowRecord record = factory.createBorrowRecord();
        record.setBook(book);
        record.setBorrowingDate(date(borrowingDate));
        record.setReturnDue(date(returnDue));
        member.getBorrowRecords().add(record);
        return record;
    }

    @Test
    public void testCase1_ReturnOwnedBook() {
        Library library = createLibrary("L17");
        Book book = createBook(library, "Ocean Currents", "Dr Blue", "B-OC");
        Member member = createMember(library, "Mike", "Stream");
        createBorrowRecord(member, book, "2025-06-25", "2025-07-09");

        boolean result = member.returnBook("Ocean Currents", date("2025-07-08"));

        assertTrue(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void testCase2_ReturnNonBorrowedTitle() {
        Library library = createLibrary("L17");
        createBook(library, "Mountain Peaks", "Alp Climb", "B-MP");
        Member member = createMember(library, "Mike", "Stream");

        boolean result = member.returnBook("Mountain Peaks", date("2025-07-10"));

        assertFalse(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void testCase3_ReturnAfterDueDatePassed() {
        Library library = createLibrary("L18");
        Book book = createBook(library, "Desert Storm", "Dune Wind", "B-DS");
        Member member = createMember(library, "Nora", "Sand");
        createBorrowRecord(member, book, "2025-07-25", "2025-08-01");

        boolean result = member.returnBook("Desert Storm", date("2025-08-05"));

        assertFalse(result);
        assertEquals(1, member.getBorrowRecords().size());
    }

    @Test
    public void testCase4_ReturnWhenNoActiveLoans() {
        Library library = createLibrary("L19");
        createBook(library, "Hidden Garden", "Leaf Light", "B-HG");
        Member member = createMember(library, "Owen", "Plot");

        boolean result = member.returnBook("Hidden Garden", date("2025-07-10"));

        assertFalse(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void testCase5_ReturnLastRemainingBook() {
        Library library = createLibrary("L20");
        Book book = createBook(library, "Polar Night", "Arctic Star", "B-PN");
        Member member = createMember(library, "Paula", "Frost");
        createBorrowRecord(member, book, "2025-08-10", "2025-08-17");

        boolean result = member.returnBook("Polar Night", date("2025-08-11"));

        assertTrue(result);
        assertTrue(member.getBorrowRecords().isEmpty());
    }
}
