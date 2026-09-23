import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null) {
            return false;
        }
        Document doc = library.findDocumentByTitle(bookTitle);
        if (doc == null) {
            return false;
        }
        if (!(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        if (book.isBorrowed()) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        book.setBorrowed(true);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord targetRecord = null;
        Book targetBook = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                targetRecord = record;
                targetBook = record.getBook();
                break;
            }
        }
        if (targetRecord == null) {
            return false;
        }
        if (targetRecord.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(targetRecord);
        targetBook.setBorrowed(false);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
        }
        return titles;
    }

    public boolean hasBorrowedBook(String bookTitle) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return true;
            }
        }
        return false;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }
}
