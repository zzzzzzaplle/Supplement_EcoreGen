import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        this();
        this.firstName = firstName;
        this.surname = surname;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) return false;

        Book bookToBorrow = null;
        for (Document doc : library.getDocuments()) {
            if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                bookToBorrow = (Book) doc;
                break;
            }
        }

        if (bookToBorrow == null) return false;

        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) return false;
            }
        }

        BorrowRecord newRecord = new BorrowRecord(today, bookToBorrow);
        borrowRecords.add(newRecord);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord target = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                target = br;
                break;
            }
        }
        if (target == null) return false;
        if (target.isOverdue(today)) return false;

        borrowRecords.remove(target);
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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}
