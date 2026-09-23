import java.util.ArrayList;
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
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null || borrowRecords == null || borrowRecords.size() >= 3) return false;
        Document doc = library.findDocumentByTitle(bookTitle);
        if (!(doc instanceof Book)) return false;
        Book book = (Book) doc;
        if (library.isBookBorrowed(book)) return false;
        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecord(bookTitle);
        if (record == null || record.isOverdue(today)) return false;
        borrowRecords.remove(record);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecord(bookTitle);
        if (record == null) return null;
        Date original = record.getReturnDue();
        return record.extendDueDate(today) != null ? record.getReturnDue() : original;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        if (borrowRecords != null) {
            for (BorrowRecord record : borrowRecords) {
                if (record != null && record.getBook() != null && record.getBook().getTitle() != null) titles.add(record.getBook().getTitle());
            }
        }
        return titles;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }

    private BorrowRecord findBorrowRecord(String bookTitle) {
        if (bookTitle == null || borrowRecords == null) return null;
        for (BorrowRecord record : borrowRecords) {
            if (record != null && record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) return record;
        }
        return null;
    }
}
