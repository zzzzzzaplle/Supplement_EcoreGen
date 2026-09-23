import java.util.*;

public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Member() {}

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) return false;
        
        Book bookToBorrow = null;
        for (Document doc : library.getDocuments()) {
            if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                bookToBorrow = (Book) doc;
                break;
            }
        }
        
        if (bookToBorrow == null) return false;
        
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) return false;
            }
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date dueDate = cal.getTime();
        
        BorrowRecord record = new BorrowRecord(today, bookToBorrow);
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord toRemove = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) return false;
                toRemove = br;
                break;
            }
        }
        if (toRemove == null) return false;
        borrowRecords.remove(toRemove);
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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}
