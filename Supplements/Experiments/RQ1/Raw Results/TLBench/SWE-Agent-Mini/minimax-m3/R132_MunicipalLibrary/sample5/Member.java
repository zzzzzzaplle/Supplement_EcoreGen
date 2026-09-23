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

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (bookTitle == null || library == null || today == null) {
            return false;
        }
        // Already holds 3 books
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
        if (isBookBorrowedByAnyone(book)) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord found = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                found = br;
                break;
            }
        }
        if (found == null) {
            return false;
        }
        if (found.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(found);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
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
            if (br.getBook() != null && br.getBook().getTitle() != null) {
                titles.add(br.getBook().getTitle());
            }
        }
        return titles;
    }

    private boolean isBookBorrowedByAnyone(Book book) {
        if (library == null) return false;
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook() == book) {
                    return true;
                }
            }
        }
        return false;
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
