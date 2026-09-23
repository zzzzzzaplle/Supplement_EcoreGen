import java.text.SimpleDateFormat;
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
        if (library == null || bookTitle == null || today == null) return false;
        if (borrowRecords.size() >= 3) return false;
        Document found = null;
        for (Document d : library.getDocuments()) {
            if (d != null && bookTitle.equals(d.getTitle()) && d instanceof Book) {
                found = d;
                break;
            }
        }
        if (found == null) return false;
        Book book = (Book) found;
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br != null && br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                    return false;
                }
            }
        }
        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) return false;
        BorrowRecord target = null;
        for (BorrowRecord br : borrowRecords) {
            if (br != null && br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                target = br;
                break;
            }
        }
        if (target == null || target.isOverdue(today)) return false;
        borrowRecords.remove(target);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) return null;
        for (BorrowRecord br : borrowRecords) {
            if (br != null && br.getBook() != null && bookTitle.equals(br.getBook().getTitle())) {
                return br.extendDueDate(today);
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord br : borrowRecords) {
            if (br != null && br.getBook() != null) titles.add(br.getBook().getTitle());
        }
        return titles;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member m = (Member) o;
        return safe(firstName).equals(safe(m.firstName)) && safe(surname).equals(safe(m.surname));
    }

    public int hashCode() { return safe(firstName).hashCode() * 31 + safe(surname).hashCode(); }
    private String safe(String s) { return s == null ? "" : s; }
}
