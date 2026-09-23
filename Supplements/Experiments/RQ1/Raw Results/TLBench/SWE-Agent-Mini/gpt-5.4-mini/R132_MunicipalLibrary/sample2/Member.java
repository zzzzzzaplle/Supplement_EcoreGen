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

    public Member(String firstName, String surname, Library library) {
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords == null) {
            borrowRecords = new ArrayList<BorrowRecord>();
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Document found = null;
        for (Document d : library.getDocuments()) {
            if (d != null && bookTitle.equals(d.getTitle())) {
                found = d;
                break;
            }
        }
        if (!(found instanceof Book)) {
            return false;
        }
        for (Member m : library.getMembers()) {
            if (m != null && m.getBorrowRecords() != null) {
                for (BorrowRecord r : m.getBorrowRecords()) {
                    if (r != null && r.getBook() != null && bookTitle.equals(r.getBook().getTitle())) {
                        return false;
                    }
                }
            }
        }
        borrowRecords.add(new BorrowRecord(today, (Book) found));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return false;
        }
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord r = borrowRecords.get(i);
            if (r != null && r.getBook() != null && bookTitle.equals(r.getBook().getTitle())) {
                if (r.isOverdue(today)) {
                    return false;
                }
                borrowRecords.remove(i);
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return null;
        }
        for (BorrowRecord r : borrowRecords) {
            if (r != null && r.getBook() != null && bookTitle.equals(r.getBook().getTitle())) {
                return r.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        if (borrowRecords == null) {
            return titles;
        }
        for (BorrowRecord r : borrowRecords) {
            if (r != null && r.getBook() != null && r.getBook().getTitle() != null) {
                titles.add(r.getBook().getTitle());
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
