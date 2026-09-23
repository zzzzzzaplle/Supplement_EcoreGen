import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (bookTitle == null || library == null) {
            return false;
        }
        Document doc = null;
        for (Document d : library.getDocuments()) {
            if (d.getTitle() != null && d.getTitle().equals(bookTitle)) {
                doc = d;
                break;
            }
        }
        if (doc == null) {
            return false;
        }
        if (!(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook() != null && br.getBook().getTitle() != null
                        && br.getBook().getTitle().equals(bookTitle)) {
                    return false;
                }
            }
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null) {
            return false;
        }
        BorrowRecord toReturn = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && br.getBook().getTitle() != null
                    && br.getBook().getTitle().equals(bookTitle)) {
                toReturn = br;
                break;
            }
        }
        if (toReturn == null) {
            return false;
        }
        if (toReturn.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(toReturn);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null) {
            return null;
        }
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && br.getBook().getTitle() != null
                    && br.getBook().getTitle().equals(bookTitle)) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook() != null && br.getBook().getTitle() != null) {
                titles.add(br.getBook().getTitle());
            }
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
