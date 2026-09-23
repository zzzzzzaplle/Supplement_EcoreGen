import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        Document doc = findDocumentByTitle(library.getDocuments(), bookTitle);
        if (doc == null || !(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        if (isBookBorrowedByAnyone(book)) {
            return false;
        }
        if (alreadyHoldsTitle(bookTitle)) {
            return false;
        }
        Date dueDate = addDays(today, 7);
        BorrowRecord record = new BorrowRecord(today, book);
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) {
            return false;
        }
        if (record.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(record);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return null;
        }
        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) {
            return null;
        }
        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord r : borrowRecords) {
            if (r.getBook() != null) {
                titles.add(r.getBook().getTitle());
            }
        }
        return titles;
    }

    private Document findDocumentByTitle(java.util.Set<Document> docs, String title) {
        if (docs == null) {
            return null;
        }
        for (Document d : docs) {
            if (d != null && d.getTitle() != null && d.getTitle().equals(title)) {
                return d;
            }
        }
        return null;
    }

    private boolean isBookBorrowedByAnyone(Book book) {
        if (library == null || library.getMembers() == null) {
            return false;
        }
        for (Member m : library.getMembers()) {
            if (m == null || m.getBorrowRecords() == null) {
                continue;
            }
            for (BorrowRecord r : m.getBorrowRecords()) {
                if (r != null && r.getBook() != null && r.getBook().equals(book)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean alreadyHoldsTitle(String title) {
        for (BorrowRecord r : borrowRecords) {
            if (r != null && r.getBook() != null && title.equals(r.getBook().getTitle())) {
                return true;
            }
        }
        return false;
    }

    private BorrowRecord findBorrowRecordByTitle(String title) {
        for (BorrowRecord r : borrowRecords) {
            if (r != null && r.getBook() != null && title.equals(r.getBook().getTitle())) {
                return r;
            }
        }
        return null;
    }

    private static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
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
