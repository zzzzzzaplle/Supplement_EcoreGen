import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
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
        if (library == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Book book = library.findBorrowableBookByTitle(bookTitle);
        if (book == null) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord record = findRecordByTitle(bookTitle);
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
        BorrowRecord record = findRecordByTitle(bookTitle);
        if (record == null) {
            return record == null ? null : record.getReturnDue();
        }
        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
        }
        return titles;
    }

    private BorrowRecord findRecordByTitle(String bookTitle) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
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
}
