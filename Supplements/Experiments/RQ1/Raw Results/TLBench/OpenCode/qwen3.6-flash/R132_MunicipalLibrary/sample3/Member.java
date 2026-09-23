import java.util.*;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        borrowRecords = new ArrayList<>();
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

    public boolean borrowBook(String bookTitle, java.util.Date today) {
        Book book = library.findBookByTitle(bookTitle);
        if (book == null) {
            return false;
        }
        if (!(book instanceof Book)) {
            return false;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().equals(book)) {
                return false;
            }
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, java.util.Date today) {
        BorrowRecord target = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                target = record;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (target.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(target);
        return true;
    }

    public java.util.Date extendReturnDueDate(String bookTitle, java.util.Date today) {
        BorrowRecord target = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                target = record;
                break;
            }
        }
        if (target == null) {
            return null;
        }
        if (today.before(target.getBorrowingDate()) || !today.before(target.getReturnDue())) {
            return target.getReturnDue();
        }
        target.extendDueDate(today);
        return target.getReturnDue();
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
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
