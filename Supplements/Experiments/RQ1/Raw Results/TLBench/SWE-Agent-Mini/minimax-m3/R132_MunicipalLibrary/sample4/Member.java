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
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Book foundBook = null;
        for (Document d : library.getDocuments()) {
            if (d instanceof Book) {
                Book b = (Book) d;
                if (b.getTitle() != null && b.getTitle().equals(bookTitle)) {
                    foundBook = b;
                    break;
                }
            }
        }
        if (foundBook == null) {
            return false;
        }
        for (Member m : library.getMembers()) {
            for (BorrowRecord r : m.getBorrowRecords()) {
                Book br = r.getBook();
                if (br != null && br.getTitle() != null && br.getTitle().equals(bookTitle)) {
                    return false;
                }
            }
        }
        BorrowRecord record = new BorrowRecord(today, foundBook);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord toReturn = null;
        for (BorrowRecord r : borrowRecords) {
            Book b = r.getBook();
            if (b != null && b.getTitle() != null && b.getTitle().equals(bookTitle)) {
                toReturn = r;
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
        if (bookTitle == null || today == null) {
            return null;
        }
        for (BorrowRecord r : borrowRecords) {
            Book b = r.getBook();
            if (b != null && b.getTitle() != null && b.getTitle().equals(bookTitle)) {
                return r.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord r : borrowRecords) {
            Book b = r.getBook();
            if (b != null && b.getTitle() != null) {
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
