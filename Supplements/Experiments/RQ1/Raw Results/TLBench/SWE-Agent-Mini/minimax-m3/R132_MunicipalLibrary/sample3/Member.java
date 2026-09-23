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
    
    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }
    
    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }
    
    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        // Check max 3 books
        if (borrowRecords.size() >= 3) {
            return false;
        }
        // Check if title exists and is a book
        Document doc = library.findDocumentByTitle(bookTitle);
        if (doc == null || !(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        // Check if book is already borrowed
        if (library.isBookBorrowed(book)) {
            return false;
        }
        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        library.registerBorrowRecord(record);
        return true;
    }
    
    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            Book book = record.getBook();
            if (book != null && bookTitle.equals(book.getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                borrowRecords.remove(i);
                library.unregisterBorrowRecord(record);
                return true;
            }
        }
        return false;
    }
    
    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return null;
        }
        for (BorrowRecord record : borrowRecords) {
            Book book = record.getBook();
            if (book != null && bookTitle.equals(book.getTitle())) {
                return record.extendDueDate(today);
            }
        }
        return null;
    }
    
    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord record : borrowRecords) {
            Book book = record.getBook();
            if (book != null) {
                titles.add(book.getTitle());
            }
        }
        return titles;
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
