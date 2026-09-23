import java.util.*;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) return false;
        
        Document doc = null;
        for (Document d : library.getDocuments()) {
            if (d.getTitle().equals(bookTitle)) {
                doc = d;
                break;
            }
        }
        if (!(doc instanceof Book)) return false;
        
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) return false;
            }
        }

        BorrowRecord br = new BorrowRecord(today, (Book) doc);
        borrowRecords.add(br);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord recordToRemove = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) return false;
                recordToRemove = br;
                break;
            }
        }
        if (recordToRemove == null) return false;
        borrowRecords.remove(recordToRemove);
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

    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}
