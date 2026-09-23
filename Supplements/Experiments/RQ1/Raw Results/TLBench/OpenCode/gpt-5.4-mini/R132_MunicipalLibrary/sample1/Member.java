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
        if (library == null || bookTitle == null || today == null || borrowRecords == null || borrowRecords.size() >= 3) {
            return false;
        }
        Document target = null;
        for (Document document : library.getDocuments()) {
            if (bookTitle.equals(document.getTitle()) && document instanceof Book) {
                target = document;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                    return false;
                }
            }
        }
        BorrowRecord record = new BorrowRecord(today, (Book) target);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord found = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                found = record;
                break;
            }
        }
        if (found == null || found.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(found);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return null;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                Date original = record.getReturnDue();
                return record.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null) {
                titles.add(record.getBook().getTitle());
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
