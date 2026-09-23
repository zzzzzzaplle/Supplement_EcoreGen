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
        if (bookTitle == null || today == null) {
            return false;
        }
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<BorrowRecord>();
        }
        if (this.borrowRecords.size() >= 3) {
            return false;
        }
        Document doc = this.library.findDocumentByTitle(bookTitle);
        if (doc == null) {
            return false;
        }
        if (!(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        if (this.library.isBookBorrowed(book)) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, book);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        if (this.borrowRecords == null) {
            return false;
        }
        BorrowRecord toReturn = null;
        for (BorrowRecord r : this.borrowRecords) {
            Book b = r.getBook();
            if (b != null && bookTitle.equals(b.getTitle())) {
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
        this.borrowRecords.remove(toReturn);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return null;
        }
        if (this.borrowRecords == null) {
            return null;
        }
        for (BorrowRecord r : this.borrowRecords) {
            Book b = r.getBook();
            if (b != null && bookTitle.equals(b.getTitle())) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        Member m = (Member) o;
        boolean firstEq = (this.firstName == null) ? (m.firstName == null) : this.firstName.equals(m.firstName);
        boolean surnameEq = (this.surname == null) ? (m.surname == null) : this.surname.equals(m.surname);
        return firstEq && surnameEq;
    }

    @Override
    public int hashCode() {
        int result = (this.firstName == null) ? 0 : this.firstName.hashCode();
        result = 31 * result + ((this.surname == null) ? 0 : this.surname.hashCode());
        return result;
    }
}
