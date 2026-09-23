package edu.municipalLibrary.test;

import static org.junit.Assert.assertEquals;

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

public class CR3Test {

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
    public void testCase1_EligibleForExtension() {
        Library library = createLibrary("L12");
        Member member = createMember(library, "Hugh", "Bright");
        Book book = createBook(library, "Solar Physics", "Dr Ray", "B-SP");
        createBorrowRecord(member, book, "2025-01-01", "2025-01-08");

        Date result = member.extendReturnDueDate("Solar Physics", date("2025-01-05"));

        assertEquals(date("2025-01-15"), result);
        assertEquals(date("2025-01-15"), member.getBorrowRecords().get(0).getReturnDue());
    }

    @Test
    public void testCase2_TodayEqualsDueDate() {
        Library library = createLibrary("L13");
        Member member = createMember(library, "Ian", "Saga");
        Book book = createBook(library, "Ancient Myths", "Hist Lore", "B-AM");
        createBorrowRecord(member, book, "2025-02-03", "2025-02-10");

        Date result = member.extendReturnDueDate("Ancient Myths", date("2025-02-10"));

        assertEquals(date("2025-02-10"), result);
        assertEquals(date("2025-02-10"), member.getBorrowRecords().get(0).getReturnDue());
    }

    @Test
    public void testCase3_TodayBeforeBorrowingDate() {
        Library library = createLibrary("L14");
        Member member = createMember(library, "Jill", "Matrix");
        Book book = createBook(library, "Linear Algebra", "Prof Calc", "B-LA");
        createBorrowRecord(member, book, "2025-03-05", "2025-03-12");

        Date result = member.extendReturnDueDate("Linear Algebra", date("2025-03-03"));

        assertEquals(date("2025-03-12"), result);
        assertEquals(date("2025-03-12"), member.getBorrowRecords().get(0).getReturnDue());
    }

    @Test
    public void testCase4_MultipleBorrowingByMember() {
        Library library = createLibrary("L15");
        Member member = createMember(library, "Karl", "Paint");
        Book modernArt = createBook(library, "Modern Art", "Lores", "B-MA");
        Book colorTheory = createBook(library, "Color Theory", "Lores", "B-CT");
        createBorrowRecord(member, modernArt, "2025-04-01", "2025-04-08");
        createBorrowRecord(member, colorTheory, "2025-04-02", "2025-04-09");

        Date result = member.extendReturnDueDate("Modern Art", date("2025-04-05"));

        assertEquals(date("2025-04-15"), result);
        assertEquals(date("2025-04-15"), member.getBorrowRecords().get(0).getReturnDue());
        assertEquals(date("2025-04-09"), member.getBorrowRecords().get(1).getReturnDue());
    }

    @Test
    public void testCase5_SecondExtensionAttempt() {
        Library library = createLibrary("L16");
        Member member = createMember(library, "Lara", "Wave");
        Book book = createBook(library, "Quantum Fields", "Phys Mind", "B-QF");
        createBorrowRecord(member, book, "2025-05-01", "2025-05-22");

        Date result = member.extendReturnDueDate("Quantum Fields", date("2025-05-18"));

        assertEquals(date("2025-05-29"), result);
        assertEquals(date("2025-05-29"), member.getBorrowRecords().get(0).getReturnDue());
    }
}
