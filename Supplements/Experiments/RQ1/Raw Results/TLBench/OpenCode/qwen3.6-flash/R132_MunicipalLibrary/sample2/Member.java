import java.util.*;

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

    public boolean borrowBook(String bookTitle, Date today) {
        Document doc = library.findDocumentByTitle(bookTitle);
        if (doc == null) {
            return false;
        }
        if (!(doc instanceof Book)) {
            return false;
        }
        Book book = (Book) doc;
        for (Member m : library.getMembers()) {
            for (BorrowRecord r : m.getBorrowRecords()) {
                if (book.equals(r.getBook())) {
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
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (bookTitle.equals(record.getBook().getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                borrowRecords.remove(record);
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (bookTitle.equals(record.getBook().getTitle())) {
                if (!today.before(record.getBorrowingDate()) && today.before(record.getReturnDue())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(record.getReturnDue());
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    Date newDue = cal.getTime();
                    record.setReturnDue(newDue);
                    return newDue;
                }
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
}
