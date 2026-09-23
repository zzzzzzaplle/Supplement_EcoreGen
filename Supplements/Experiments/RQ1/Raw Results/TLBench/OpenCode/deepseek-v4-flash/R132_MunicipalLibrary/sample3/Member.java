import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Document doc = library.findDocumentByTitle(bookTitle);
        if (doc == null || !(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        if (library.isBookBorrowed(book)) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecord(bookTitle);
        if (record == null) {
            return false;
        }
        if (record.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(record);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecord(bookTitle);
        if (record == null) {
            return null;
        }
        if (!today.before(record.getReturnDue())) {
            return record.getReturnDue();
        }
        if (today.before(record.getBorrowingDate())) {
            return record.getReturnDue();
        }
        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
        }
        return titles;
    }

    private BorrowRecord findBorrowRecord(String bookTitle) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record;
            }
        }
        return null;
    }

    public boolean holdsBook(Book book) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().equals(book)) {
                return true;
            }
        }
        return false;
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
