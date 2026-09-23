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
        if (this.library == null) {
            return false;
        }
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<BorrowRecord>();
        }
        if (this.borrowRecords.size() >= 3) {
            return false;
        }
        Document doc = null;
        for (Document d : this.library.getDocuments()) {
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
        for (Member m : this.library.getMembers()) {
            List<BorrowRecord> records = m.getBorrowRecords();
            if (records == null) {
                continue;
            }
            for (BorrowRecord r : records) {
                if (r.getBook() == book) {
                    return false;
                }
            }
        }
        BorrowRecord record = new BorrowRecord(today, book);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (this.borrowRecords == null) {
            return false;
        }
        BorrowRecord found = null;
        for (BorrowRecord r : this.borrowRecords) {
            Book b = r.getBook();
            if (b != null && b.getTitle() != null && b.getTitle().equals(bookTitle)) {
                found = r;
                break;
            }
        }
        if (found == null) {
            return false;
        }
        if (found.isOverdue(today)) {
            return false;
        }
        this.borrowRecords.remove(found);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (this.borrowRecords == null) {
            return null;
        }
        for (BorrowRecord r : this.borrowRecords) {
            Book b = r.getBook();
            if (b != null && b.getTitle() != null && b.getTitle().equals(bookTitle)) {
                return r.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        if (this.borrowRecords == null) {
            return titles;
        }
        for (BorrowRecord r : this.borrowRecords) {
            Book b = r.getBook();
            if (b != null) {
                titles.add(b.getTitle());
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
