import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Represents the municipal library and its registered members and documents.
 */
 class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public Library(String name) {
        this();
        this.name = name;
    }

    /**
     * Registers a new member if the name is valid and not already present.
     *
     * @param firstName first name
     * @param surname surname
     * @return true if registration succeeds, false otherwise
     */
    public boolean registerMember(String firstName, String surname) {
        if (!isValidName(firstName) || !isValidName(surname)) {
            return false;
        }
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member m : members) {
            String existing = safeTrim(m.getFirstName()) + " " + safeTrim(m.getSurname());
            if (fullName.equals(existing)) {
                return false;
            }
        }
        Member member = new Member(firstName.trim(), surname.trim(), this);
        members.add(member);
        return true;
    }

    /**
     * Adds a document to the library collection.
     *
     * @param doc document to add
     */
    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> m) {
        this.members = m;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> d) {
        this.documents = d;
    }

    private boolean isValidName(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}

/**
 * Represents a registered library member.
 */
class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    /**
     * Borrows a book by title if possible.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return true if borrowing succeeds
     */
    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }

        Document found = null;
        for (Document d : library.getDocuments()) {
            if (bookTitle.equals(d.getTitle())) {
                found = d;
                break;
            }
        }

        if (!(found instanceof Book)) {
            return false;
        }

        Book book = (Book) found;

        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                    return false;
                }
            }
        }

        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    /**
     * Returns a borrowed book by title if the book is held and not overdue.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return true if return succeeds
     */
    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                borrowRecords.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Extends the due date of a borrowed book if permitted.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return new due date if extended, otherwise original due date
     */
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

    /**
     * Lists titles of all currently borrowed books.
     *
     * @return list of borrowed book titles
     */
    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook() != null && record.getBook().getTitle() != null) {
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

/**
 * Represents a borrowing record for a borrowed book.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        if (borrowingDate != null) {
            this.returnDue = addDays(borrowingDate, 7);
        }
    }

    /**
     * Extends the due date by seven days if today is on or after borrowing date
     * and strictly before the current due date.
     *
     * @param today current date
     * @return updated due date or original due date
     */
    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return returnDue;
        }
        if (!today.before(borrowingDate) && today.before(returnDue)) {
            returnDue = addDays(returnDue, 7);
        }
        return returnDue;
    }

    /**
     * Checks whether the record is overdue with respect to today.
     *
     * @param today current date
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.after(returnDue);
    }

    public Date getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public Date getReturnDue() {
        return returnDue;
    }

    public void setReturnDue(Date returnDue) {
        this.returnDue = returnDue;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    private Date addDays(Date date, int days) {
        long millis = date.getTime() + TimeUnit.DAYS.toMillis(days);
        return new Date(millis);
    }
}

/**
 * Base class for documents identified by title.
 */
abstract class Document {
    private String title;

    public Document() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * A journal document with a publication date.
 */
class Journal extends Document {
    private Date publicationDate;

    public Journal() {
        super();
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

/**
 * Base class for volumes, which are documents with an author.
 */
abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * A book volume that can be borrowed.
 */
class Book extends Volume {
    private String bookId;

    public Book() {
        super();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

/**
 * A dictionary volume.
 */
class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
        super();
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

/**
 * A comic volume.
 */
class Comic extends Volume {
    private String recipientName;

    public Comic() {
        super();
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}