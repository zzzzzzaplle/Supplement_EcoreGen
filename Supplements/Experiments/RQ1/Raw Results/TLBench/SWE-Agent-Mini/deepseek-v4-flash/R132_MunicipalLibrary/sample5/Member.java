import java.time.LocalDate;
import java.util.ArrayList;
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

    public boolean borrowBook(String bookTitle, LocalDate today) {
        if (library == null || today == null || bookTitle == null || bookTitle.trim().isEmpty()) {
            return false;
        }

        if (borrowRecords.size() >= 3) {
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

        if (library.isBookBorrowed(book)) {
            return false;
        }

        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, LocalDate today) {
        if (bookTitle == null || today == null) {
            return false;
        }

        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) {
            return false;
        }

        if (record.isOverdue(today)) {
            return false;
        }

        borrowRecords.remove(record);
        return true;
    }

    public LocalDate extendReturnDueDate(String bookTitle, LocalDate today) {
        if (bookTitle == null || today == null) {
            BorrowRecord record = findBorrowRecordByTitle(bookTitle);
            if (record != null) {
                return record.getReturnDue();
            }
            return null;
        }

        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) {
            return null;
        }

        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        return borrowRecords.stream()
                .map(r -> r.getBook().getTitle())
                .collect(Collectors.toList());
    }

    private BorrowRecord findBorrowRecordByTitle(String title) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(title)) {
                return record;
            }
        }
        return null;
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
