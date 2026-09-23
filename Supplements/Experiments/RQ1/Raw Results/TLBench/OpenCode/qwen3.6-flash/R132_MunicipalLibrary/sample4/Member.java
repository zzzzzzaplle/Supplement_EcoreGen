import java.util.*;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Member() {}

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        Document targetDoc = null;
        for (Document d : library.getDocuments()) {
            if (d.getTitle().equals(bookTitle)) {
                targetDoc = d;
                break;
            }
        }

        if (targetDoc == null || !(targetDoc instanceof Book)) {
            return false;
        }

        Book book = (Book) targetDoc;

        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) {
                    return false;
                }
            }
        }

        if (borrowRecords.size() >= 3) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date dueDate = cal.getTime();

        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord targetRec = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                targetRec = br;
                break;
            }
        }

        if (targetRec == null) {
            return false;
        }

        if (targetRec.isOverdue(today)) {
            return false;
        }

        borrowRecords.remove(targetRec);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            titles.add(br.getBook().getTitle());
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
