import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        // Find the book by title in the library's documents
        if (library == null) {
            return false;
        }
        Book bookToBorrow = null;
        for (Document doc : library.getDocuments()) {
            if (doc instanceof Book && doc.getTitle() != null && doc.getTitle().equals(bookTitle)) {
                bookToBorrow = (Book) doc;
                break;
            }
        }
        if (bookToBorrow == null) {
            return false;
        }
        // Check the book is not already borrowed by any member
        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook().getTitle().equals(bookTitle)) {
                    return false; // Already borrowed
                }
            }
        }
        // Check member holds fewer than three books
        if (borrowRecords.size() >= 3) {
            return false;
        }
        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, bookToBorrow);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord toRemove = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                toRemove = record;
                break;
            }
        }
        if (toRemove == null) {
            return false; // Member does not currently hold that title
        }
        if (toRemove.isOverdue(today)) {
            return false; // Book is overdue, return rejected
        }
        borrowRecords.remove(toRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record.extendDueDate(today);
            }
        }
        // If no such record, return null? The spec says returns the original due date if conditions not met.
        // But if no record exists, we should probably return null.
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        return borrowRecords.stream()
                .map(record -> record.getBook().getTitle())
                .collect(Collectors.toList());
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
