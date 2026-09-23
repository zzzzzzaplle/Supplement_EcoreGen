import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Represents a municipal library that manages members and documents.
 */
class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    /**
     * Constructs an empty library.
     */
    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    /**
     * Registers a new member if the name is valid and unique.
     *
     * @param firstName the member's first name
     * @param surname the member's surname
     * @return true if registration succeeds; false otherwise
     */
    public boolean registerMember(String firstName, String surname) {
        if (!isValidName(firstName) || !isValidName(surname)) {
            return false;
        }

        String normalizedFullName = normalize(firstName) + " " + normalize(surname);
        for (Member member : members) {
            String existingFullName = normalize(member.getFirstName()) + " " + normalize(member.getSurname());
            if (existingFullName.equals(normalizedFullName)) {
                return false;
            }
        }

        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    /**
     * Adds a document to the library's collection.
     *
     * @param doc the document to add
     */
    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    /**
     * Finds a document by title.
     *
     * @param title the title to search for
     * @return the matching document, or null if none exists
     */
    public Document findDocumentByTitle(String title) {
        if (title == null) {
            return null;
        }
        for (Document document : documents) {
            if (title.equals(document.getTitle())) {
                return document;
            }
        }
        return null;
    }

    /**
     * Determines whether a given book is currently borrowed by any member.
     *
     * @param book the book to check
     * @return true if borrowed; false otherwise
     */
    public boolean isBookBorrowed(Book book) {
        if (book == null) {
            return false;
        }
        for (Member member : members) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() == book) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the library name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the library name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the set of members.
     *
     * @return the members set
     */
    public Set<Member> getMembers() {
        return members;
    }

    /**
     * Sets the set of members.
     *
     * @param members the members set
     */
    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    /**
     * Gets the set of documents.
     *
     * @return the documents set
     */
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * Sets the set of documents.
     *
     * @param documents the documents set
     */
    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    private boolean isValidName(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}

/**
 * Represents a member of the library.
 */
class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    /**
     * Constructs an empty member.
     */
    public Member() {
        this.borrowRecords = new ArrayList<>();
    }

    /**
     * Constructs a member with the given details and library.
     *
     * @param firstName the first name
     * @param surname the surname
     * @param library the library
     */
    public Member(String firstName, String surname, Library library) {
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    /**
     * Constructs a member with the given details.
     *
     * @param firstName the first name
     * @param surname the surname
     */
    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    /**
     * Borrows a book by title on the given date.
     *
     * @param bookTitle the title of the book
     * @param today the current date
     * @return true if borrowing succeeds; false otherwise
     */
    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }

        Document document = library.findDocumentByTitle(bookTitle);
        if (!(document instanceof Book)) {
            return false;
        }

        Book book = (Book) document;
        if (library.isBookBorrowed(book)) {
            return false;
        }

        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    /**
     * Returns a borrowed book by title.
     *
     * @param bookTitle the title of the book
     * @param today the current date
     * @return true if the return succeeds; false otherwise
     */
    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }

        Iterator<BorrowRecord> iterator = borrowRecords.iterator();
        while (iterator.hasNext()) {
            BorrowRecord record = iterator.next();
            if (record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Extends the due date of one borrowed book.
     *
     * @param bookTitle the title of the book
     * @param today the current date
     * @return the new due date if extended; otherwise the original due date
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
     * Lists the titles of all currently borrowed books.
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

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname.
     *
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the borrow records.
     *
     * @return the borrow records list
     */
    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    /**
     * Sets the borrow records.
     *
     * @param borrowRecords the borrow records list
     */
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }

    /**
     * Gets the library reference.
     *
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Sets the library reference.
     *
     * @param library the library to set
     */
    public void setLibrary(Library library) {
        this.library = library;
    }
}

/**
 * Represents a borrow record for a book.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    /**
     * Constructs an empty borrow record.
     */
    public BorrowRecord() {
    }

    /**
     * Constructs a borrow record with borrowing date and book.
     *
     * @param borrowingDate the borrowing date
     * @param book the borrowed book
     */
    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = addDays(borrowingDate, 7);
    }

    /**
     * Extends the due date by seven days if today is before the current due date.
     *
     * @param today the current date
     * @return the new due date if extended; otherwise the original due date
     */
    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return returnDue;
        }
        if (!today.before(returnDue)) {
            return returnDue;
        }
        returnDue = addDays(returnDue, 7);
        return returnDue;
    }

    /**
     * Checks whether the borrow record is overdue on the given date.
     *
     * @param today the date to check
     * @return true if overdue; false otherwise
     */
    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.after(returnDue);
    }

    /**
     * Gets the borrowing date.
     *
     * @return the borrowing date
     */
    public Date getBorrowingDate() {
        return borrowingDate;
    }

    /**
     * Sets the borrowing date.
     *
     * @param borrowingDate the borrowing date to set
     */
    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    /**
     * Gets the return due date.
     *
     * @return the due date
     */
    public Date getReturnDue() {
        return returnDue;
    }

    /**
     * Sets the return due date.
     *
     * @param returnDue the due date to set
     */
    public void setReturnDue(Date returnDue) {
        this.returnDue = returnDue;
    }

    /**
     * Gets the borrowed book.
     *
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the borrowed book.
     *
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }

    private Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }
}

/**
 * Base class for all documents.
 */
abstract class Document {
    private String title;

    /**
     * Constructs an empty document.
     */
    public Document() {
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * Represents a journal document.
 */
class Journal extends Document {
    private Date publicationDate;

    /**
     * Constructs an empty journal.
     */
    public Journal() {
        super();
    }

    /**
     * Gets the publication date.
     *
     * @return the publication date
     */
    public Date getPublicationDate() {
        return publicationDate;
    }

    /**
     * Sets the publication date.
     *
     * @param publicationDate the publication date to set
     */
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

/**
 * Base class for volume-type documents.
 */
abstract class Volume extends Document {
    private String author;

    /**
     * Constructs an empty volume.
     */
    public Volume() {
        super();
    }

    /**
     * Gets the author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     *
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * Represents a book volume.
 */
class Book extends Volume {
    private String bookId;

    /**
     * Constructs an empty book.
     */
    public Book() {
        super();
    }

    /**
     * Gets the book identifier.
     *
     * @return the bookId
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets the book identifier.
     *
     * @param bookId the bookId to set
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

/**
 * Represents a dictionary volume.
 */
class Dictionary extends Volume {
    private String dictionaryId;

    /**
     * Constructs an empty dictionary.
     */
    public Dictionary() {
        super();
    }

    /**
     * Gets the dictionary identifier.
     *
     * @return the dictionaryId
     */
    public String getDictionaryId() {
        return dictionaryId;
    }

    /**
     * Sets the dictionary identifier.
     *
     * @param dictionaryId the dictionaryId to set
     */
    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

/**
 * Represents a comic volume.
 */
class Comic extends Volume {
    private String recipientName;

    /**
     * Constructs an empty comic.
     */
    public Comic() {
        super();
    }

    /**
     * Gets the recipient name.
     *
     * @return the recipient name
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Sets the recipient name.
     *
     * @param recipientName the recipient name to set
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}