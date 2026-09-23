package edu.municipalLibrary.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

import edu.municipalLibrary.Book;
import edu.municipalLibrary.BorrowRecord;
import edu.municipalLibrary.Library;
import edu.municipalLibrary.Member;
import edu.municipalLibrary.MunicipalLibraryFactory;

public class CR5Test {

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
    public void testCase1_MemberWithThreeLoans() {
        Library library = createLibrary("L21");
        Book marsDiary = createBook(library, "Mars Diary", "A. Astro", "B-MD");
        Book venusRoses = createBook(library, "Venus Roses", "B. Botany", "B-VR");
        Book earthChronicle = createBook(library, "Earth Chronicle", "C. Core", "B-EC");
        Member member = createMember(library, "Quinn", "Orbit");
        createBorrowRecord(member, marsDiary, "2025-03-01", "2025-03-08");
        createBorrowRecord(member, venusRoses, "2025-03-01", "2025-03-08");
        createBorrowRecord(member, earthChronicle, "2025-03-01", "2025-03-08");

        EList<String> result = member.listBorrowedBookTitles();

        assertEquals(Arrays.asList("Mars Diary", "Venus Roses", "Earth Chronicle"), result);
    }

    @Test
    public void testCase2_MemberWithNoLoans() {
        Library library = createLibrary("L22");
        Member member = createMember(library, "Rita", "Null");

        EList<String> result = member.listBorrowedBookTitles();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCase3_OneReturnedOneActive() {
        Library library = createLibrary("L23");
        Book cityLights = createBook(library, "City Lights", "D. Urban", "B-CL");
        Book moonOrbit = createBook(library, "Moon Orbit", "E. Lunar", "B-MO");
        Member member = createMember(library, "Sam", "Toggle");
        BorrowRecord returnedRecord = createBorrowRecord(member, cityLights, "2025-02-01", "2025-02-08");
        member.getBorrowRecords().remove(returnedRecord);
        createBorrowRecord(member, moonOrbit, "2025-02-28", "2025-03-07");

        EList<String> result = member.listBorrowedBookTitles();

        assertEquals(Arrays.asList("Moon Orbit"), result);
    }

    @Test
    public void testCase4_MemberAtLimit() {
        Library library = createLibrary("L24");
        Book alphaZone = createBook(library, "Alpha Zone", "F. Frontier", "B-AZ");
        Book betaDrift = createBook(library, "Beta Drift", "G. Galaxy", "B-BD");
        Book gammaRay = createBook(library, "Gamma Ray", "H. Horizon", "B-GR");
        Member member = createMember(library, "Tara", "Limit");
        createBorrowRecord(member, alphaZone, "2025-04-10", "2025-04-17");
        createBorrowRecord(member, betaDrift, "2025-04-11", "2025-04-18");
        createBorrowRecord(member, gammaRay, "2025-04-12", "2025-04-19");

        EList<String> result = member.listBorrowedBookTitles();

        assertEquals(Arrays.asList("Alpha Zone", "Beta Drift", "Gamma Ray"), result);
    }

    @Test
    public void testCase5_BrandNewMember() {
        Library library = createLibrary("L25");
        Member member = createMember(library, "Uma", "Fresh");

        EList<String> result = member.listBorrowedBookTitles();

        assertTrue(result.isEmpty());
    }
}
