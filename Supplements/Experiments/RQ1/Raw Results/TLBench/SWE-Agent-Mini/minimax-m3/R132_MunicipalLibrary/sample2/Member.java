import java.util.ArrayList;
import java.util.Calendar;
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
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null) {
            return false;
        }
        if (bookTitle == null) {
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
        // Check if the book is already borrowed by any member
        for (Member m : library.getMembers()) {
            if (m.getBorrowRecords() != null) {
                for (BorrowRecord br : m.getBorrowRecords()) {
                    if (br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                        return false;
                    }
                }
            }
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null) {
            return false;
        }
        BorrowRecord toRemove = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                if (br.isOverdue(today)) {
                    return false;
                }
                toRemove = br;
                break;
            }
        }
        if (toRemove == null) {
            return false;
        }
        borrowRecords.remove(toRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null) {
            return null;
        }
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null) {
                titles.add(br.getBook().getTitle());
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
