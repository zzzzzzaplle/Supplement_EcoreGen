package edu.municipalLibrary.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.municipalLibrary.Book;
import edu.municipalLibrary.BorrowRecord;
import edu.municipalLibrary.Comic;
import edu.municipalLibrary.Dictionary;
import edu.municipalLibrary.Journal;
import edu.municipalLibrary.Library;
import edu.municipalLibrary.Member;
import edu.municipalLibrary.MunicipalLibraryFactory;

public class CR2Test {

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

    private Dictionary createDictionary(Library library, String title, String author, String dictionaryId) {
        Dictionary dictionary = factory.createDictionary();
        dictionary.setTitle(title);
        dictionary.setAuthor(author);
        dictionary.setDictionaryId(dictionaryId);
        library.addDocument(dictionary);
        return dictionary;
    }

    private Comic createComic(Library library, String title, String author, String recipientName) {
        Comic comic = factory.createComic();
        comic.setTitle(title);
        comic.setAuthor(author);
        comic.setRecipientName(recipientName);
        library.addDocument(comic);
        return comic;
    }

    private Journal createJournal(Library library, String title, String publicationDate) {
        Journal journal = factory.createJournal();
        journal.setTitle(title);
        journal.setPublicationDate(date(publicationDate));
        library.addDocument(journal);
        return journal;
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
    public void testCase1_BorrowFirstBook() {
        Library library = createLibrary("L7");
        Book book = createBook(library, "Time Travel", "H. Wells", "B-TT");
        createDictionary(library, "Latin Lexicon", "Dr Lingua", "D-LAT");
        createComic(library, "One Shot", "Cart Man", "Teens");
        createComic(library, "Dual Worlds", "Art Gal", "Adults");
        Member member = createMember(library, "Carl", "Nova");
        Date today = date("2025-06-01");

        boolean result = member.borrowBook("Time Travel", today);

        assertTrue(result);
        assertEquals(1, member.getBorrowRecords().size());
        BorrowRecord record = member.getBorrowRecords().get(0);
        assertNotNull(record);
        assertEquals(book, record.getBook());
        assertEquals(today, record.getBorrowingDate());
        assertEquals(date("2025-06-08"), record.getReturnDue());
    }

    @Test
    public void testCase2_BookAlreadyLoanedOut() {
        Library library = createLibrary("L8");
        Book book = createBook(library, "Time Travel", "H. Wells", "B-TT");
        Member member3 = createMember(library, "Carl", "Nova");
        createBorrowRecord(member3, book, "2025-06-01", "2025-06-08");
        Member member4 = createMember(library, "Dana", "Quill");

        boolean result = member4.borrowBook("Time Travel", date("2025-06-02"));

        assertFalse(result);
        assertTrue(member4.getBorrowRecords().isEmpty());
        assertEquals(1, member3.getBorrowRecords().size());
    }

    @Test
    public void testCase3_MemberReachesBorrowingLimit() {
        Library library = createLibrary("L9");
        createBook(library, "Star Atlas A", "Sky Writer", "B-SAA");
        createBook(library, "Star Atlas B", "Sky Writer", "B-SAB");
        createBook(library, "Star Atlas C", "Sky Writer", "B-SAC");
        createBook(library, "Star Atlas", "Sky Writer", "B-SA");
        Member member = createMember(library, "Evan", "Ray");
        createBorrowRecord(member, (Book) library.getDocuments().get(0), "2025-07-07", "2025-07-14");
        createBorrowRecord(member, (Book) library.getDocuments().get(1), "2025-07-07", "2025-07-14");
        createBorrowRecord(member, (Book) library.getDocuments().get(2), "2025-07-07", "2025-07-14");

        boolean result = member.borrowBook("Star Atlas", date("2025-07-10"));

        assertFalse(result);
        assertEquals(3, member.getBorrowRecords().size());
    }

    @Test
    public void testCase4_NonBookDocumentRejected() {
        Library library = createLibrary("L10");
        createJournal(library, "Science Weekly", "2025-04-01");
        Book borrowedBook = createBook(library, "BIO Basics", "Bio Writer", "B-BIO");
        Member member = createMember(library, "Fiona", "Stone");
        createBorrowRecord(member, borrowedBook, "2025-01-25", "2025-02-01");

        boolean result = member.borrowBook("Science Weekly", date("2025-04-05"));

        assertFalse(result);
        assertEquals(1, member.getBorrowRecords().size());
    }

    @Test
    public void testCase5_SecondBookStillAllowed() {
        Library library = createLibrary("L11");
        Book deepSea = createBook(library, "Deep Sea", "O. Current", "B-DS");
        Book openOcean = createBook(library, "Open Ocean", "O. Current", "B-OO");
        Member member = createMember(library, "Gina", "Wave");
        BorrowRecord oldRecord = createBorrowRecord(member, deepSea, "2025-01-25", "2025-02-01");
        member.getBorrowRecords().remove(oldRecord);

        boolean result = member.borrowBook("Open Ocean", date("2025-05-15"));

        assertTrue(result);
        assertEquals(1, member.getBorrowRecords().size());
        BorrowRecord newRecord = member.getBorrowRecords().get(0);
        assertEquals(openOcean, newRecord.getBook());
        assertEquals(date("2025-05-15"), newRecord.getBorrowingDate());
        assertEquals(date("2025-05-22"), newRecord.getReturnDue());
    }
}
