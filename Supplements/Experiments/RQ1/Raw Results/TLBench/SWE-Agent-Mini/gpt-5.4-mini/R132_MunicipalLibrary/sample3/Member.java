import java.text.SimpleDateFormat;
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
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) return false;
        if (borrowRecords == null) borrowRecords = new ArrayList<BorrowRecord>();
        if (borrowRecords.size() >= 3) return false;
        Document found = null;
        if (library.getDocuments() != null) {
            for (Document d : library.getDocuments()) {
                if (d != null && bookTitle.equals(d.getTitle()) && d instanceof Book) {
                    found = d;
                    break;
                }
            }
        }
        if (!(found instanceof Book)) return false;
        Book book = (Book) found;
        for (Member m : library.getMembers()) {
            if (m != null && m != this) {
                for (BorrowRecord br : m.getBorrowRecords()) {
                    if (br != null && br.getBook() == book) return false;
                }
            }
        }
        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) return false;
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord br = borrowRecords.get(i);
            if (br != null && br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                if (br.isOverdue(today)) return false;
                borrowRecords.remove(i);
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) return null;
        for (BorrowRecord br : borrowRecords) {
            if (br != null && br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> result = new ArrayList<String>();
        if (borrowRecords != null) {
            for (BorrowRecord br : borrowRecords) {
                if (br != null && br.getBook() != null) result.add(br.getBook().getTitle());
            }
        }
        return result;
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
