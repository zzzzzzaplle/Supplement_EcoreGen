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
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        if (library == null) {
            return false;
        }
        // Check member has fewer than 3 books
        if (borrowRecords.size() >= 3) {
            return false;
        }
        // Find the document by title
        for (Document doc : library.getDocuments()) {
            if (bookTitle.equals(doc.getTitle())) {
                // Check it is a Book
                if (!(doc instanceof Book)) {
                    return false;
                }
                Book book = (Book) doc;
                // Check book is not already borrowed by any member
                if (isBookBorrowedByAnyMember(book)) {
                    return false;
                }
                // Create borrow record
                BorrowRecord record = new BorrowRecord(today, book);
                borrowRecords.add(record);
                return true;
            }
        }
        return false;
    }

    private boolean isBookBorrowedByAnyMember(Book book) {
        if (library == null) {
            return false;
        }
        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() != null && record.getBook().equals(book)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord foundRecord = null;
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                foundRecord = record;
                break;
            }
        }
        if (foundRecord == null) {
            return false;
        }
        // Check if overdue
        if (foundRecord.isOverdue(today)) {
            return false;
        }
        // Remove the borrow record
        borrowRecords.remove(foundRecord);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return null;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
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
