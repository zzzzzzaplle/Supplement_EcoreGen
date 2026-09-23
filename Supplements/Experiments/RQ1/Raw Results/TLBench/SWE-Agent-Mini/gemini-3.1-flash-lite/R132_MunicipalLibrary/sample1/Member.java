import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Member() {}

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        // Find document
        Document foundDoc = null;
        for (Document d : library.getDocuments()) {
            if (d.getTitle().equals(bookTitle)) {
                foundDoc = d;
                break;
            }
        }
        
        if (foundDoc == null || !(foundDoc instanceof Book)) return false;
        Book book = (Book) foundDoc;
        
        // Already borrowed?
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) {
                    return false;
                }
            }
        }
        
        if (this.borrowRecords.size() >= 3) return false;
        
        BorrowRecord record = new BorrowRecord(today, book);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) return false;
                borrowRecords.remove(br);
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            titles.add(br.getBook().getTitle());
        }
        return titles;
    }

    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}
