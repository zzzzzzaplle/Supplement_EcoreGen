import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Member() {
    }

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
        if (library == null) return false;
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
            for (BorrowRecord record : m.getBorrowRecords()) {
                if (record.getBook().getTitle().equals(bookTitle)) return false;
            }
        }

        borrowRecords.add(new BorrowRecord(today, bookToBorrow));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record.getBook().getTitle().equals(bookTitle)) {
                if (record.isOverdue(today)) return false;
                borrowRecords.remove(i);
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record.extendDueDate(today);
            }
        }
        return null; // Should handle differently if not found, but implied return type check
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
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
}
