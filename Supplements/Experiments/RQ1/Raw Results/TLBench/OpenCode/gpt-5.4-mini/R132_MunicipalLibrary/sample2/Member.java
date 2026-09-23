public class Member {
    private Library library;
    private String firstName;
    private String surname;
    private java.util.List<BorrowRecord> borrowRecords;

    public Member() {
        this.borrowRecords = new java.util.ArrayList<BorrowRecord>();
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

    public boolean borrowBook(String bookTitle, java.util.Date today) {
        if (library == null) {
            return false;
        }
        return library.borrowBook(this, bookTitle, today);
    }

    public boolean returnBook(String bookTitle, java.util.Date today) {
        if (library == null) {
            return false;
        }
        return library.returnBook(this, bookTitle, today);
    }

    public java.util.Date extendReturnDueDate(String bookTitle, java.util.Date today) {
        if (library == null) {
            return null;
        }
        return library.extendReturnDueDate(this, bookTitle, today);
    }

    public java.util.List<String> listBorrowedBookTitles() {
        java.util.List<String> titles = new java.util.ArrayList<String>();
        if (borrowRecords == null) {
            return titles;
        }
        for (BorrowRecord borrowRecord : borrowRecords) {
            if (borrowRecord != null && borrowRecord.getBook() != null && borrowRecord.getBook().getTitle() != null) {
                titles.add(borrowRecord.getBook().getTitle());
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

    public java.util.List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(java.util.List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
