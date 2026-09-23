import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.borrowRecords = new ArrayList<>();
    }

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
        if (library == null) return false;
        // Find the document by title
        Document doc = null;
        for (Document d : library.getDocuments()) {
            if (d.getTitle().equals(bookTitle)) {
                doc = d;
                break;
            }
        }
        if (doc == null) return false;
        if (!(doc instanceof Book)) return false;

        Book book = (Book) doc;

        // Check if book is already borrowed by any member
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) {
                    return false;
                }
            }
        }

        // Check if member already holds 3 books
        if (borrowRecords.size() >= 3) return false;

        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord toRemove = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) {
                    return false;
                }
                toRemove = br;
                break;
            }
        }
        if (toRemove == null) return false;
        borrowRecords.remove(toRemove);
        return true;
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
