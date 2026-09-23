import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

class Member {
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
        if (today == null) return false;

        Library lib = this.library;
        if (lib == null) return false;

        // Find the book by title
        Book targetBook = null;
        for (Document doc : lib.getDocuments()) {
            if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                targetBook = (Book) doc;
                break;
            }
        }

        if (targetBook == null) return false;

        // Check if already borrowed by this member
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() == targetBook) {
                return false;
            }
        }

        // Check if borrowed by another member
        Set<Member> members = lib.getMembers();
        for (Member member : members) {
            if (member != this) {
                for (BorrowRecord record : member.getBorrowRecords()) {
                    if (record.getBook() == targetBook) {
                        return false;
                    }
                }
            }
        }

        // Check member's limit
        if (this.borrowRecords.size() >= 3) return false;

        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, targetBook);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (today == null) return false;

        Integer index = null;
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record.getBook().getTitle().equals(bookTitle)) {
                index = i;
                break;
            }
        }

        if (index == null) return false;

        BorrowRecord record = borrowRecords.get(index);

        // Check if overdue
        if (record.isOverdue(today)) return false;

        borrowRecords.remove(index);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (today == null) return null;

        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                Date originalDue = record.getReturnDue();
                record.extendDueDate(today);
                return record.getReturnDue();
            }
        }

        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
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
}
