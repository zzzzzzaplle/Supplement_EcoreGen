import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
     * Creates an empty library.
     */
    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    /**
     * Registers a new member if the full name is valid and not already present.
     *
     * @param firstName first name
     * @param surname surname
     * @return true if registration succeeds, otherwise false
     */
    public boolean registerMember(String firstName, String surname) {
        if (!isValidNamePart(firstName) || !isValidNamePart(surname)) {
            return false;
        }
        if (members == null) {
            members = new HashSet<>();
        }
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member member : members) {
            if (member != null) {
                String existingFullName = safeTrim(member.getFirstName()) + " " + safeTrim(member.getSurname());
                if (fullName.equals(existingFullName)) {
                    return false;
                }
            }
        }
        Member member = new Member(firstName.trim(), surname.trim(), this);
        members.add(member);
        return true;
    }

    /**
     * Adds a document to the library collection.
     *
     * @param doc the document to add
     */
    public void addDocument(Document doc) {
        if (documents == null) {
            documents = new HashSet<>();
        }
        if (doc != null) {
            documents.add(doc);
        }
    }

    private Document findDocumentByTitle(String title) {
        if (title == null || documents == null) {
            return null;
        }
        for (Document document : documents) {
            if (document != null && Objects.equals(document.getTitle(), title)) {
                return document;
            }
        }
        return null;
    }

    private Member findMemberByName(String firstName, String surname) {
        if (members == null) {
            return null;
        }
        for (Member member : members) {
            if (member != null
                    && Objects.equals(member.getFirstName(), firstName)
                    && Objects.equals(member.getSurname(), surname)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Gets the library name.
     *
     * @return library name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the library name.
     *
     * @param name library name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the registered members.
     *
     * @return members set
     */
    public Set<Member> getMembers() {
        return members;
    }

    /**
     * Sets the registered members.
     *
     * @param m members set
     */
    public void setMembers(Set<Member> m) {
        this.members = m;
    }

    /**
     * Gets the documents.
     *
     * @return documents set
     */
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * Sets the documents.
     *
     * @param d documents set
     */
    public void setDocuments(Set<Document> d) {
        this.documents = d;
    }

    private boolean isValidNamePart(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}

/**
 * Represents a library member capable of borrowing and returning books.
 */
class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    /**
     * Creates a member with given names and library.
     *
     * @param firstName first name
     * @param surname surname
     * @param library library reference
     */
    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    /**
     * Creates a member without an associated library.
     *
     * @param firstName first name
     * @param surname surname
     */
    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

    /**
     * Borrows a book by title if allowed by the rules.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return true if borrowing succeeds, otherwise false
     */
    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords == null) {
            borrowRecords = new ArrayList<>();
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Document document = library.findDocumentByTitle(bookTitle);
        if (!(document instanceof Book)) {
            return false;
        }
        Book book = (Book) document;
        if (isBookAlreadyBorrowed(book)) {
            return false;
        }
        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    /**
     * Returns a borrowed book by title if not overdue.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return true if return succeeds, otherwise false
     */
    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return false;
        }
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record != null && record.getBook() != null && Objects.equals(record.getBook().getTitle(), bookTitle)) {
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
     * Extends the due date of a borrowed book by seven days when permitted.
     *
     * @param bookTitle title of the book
     * @param today current date
     * @return the new due date if extended, otherwise the original due date
     */
    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return null;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record != null && record.getBook() != null && Objects.equals(record.getBook().getTitle(), bookTitle)) {
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
        if (borrowRecords == null) {
            return titles;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record != null && record.getBook() != null && record.getBook().getTitle() != null) {
                titles.add(record.getBook().getTitle());
            }
        }
        return titles;
    }

    private boolean isBookAlreadyBorrowed(Book book) {
        if (library == null || library.getMembers() == null || book == null) {
            return false;
        }
        for (Member member : library.getMembers()) {
            if (member != null && member.borrowRecords != null) {
                for (BorrowRecord record : member.borrowRecords) {
                    if (record != null && record.getBook() != null && Objects.equals(record.getBook().getTitle(), book.getTitle())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the first name.
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the surname.
     *
     * @return surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname.
     *
     * @param surname surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the borrow records.
     *
     * @return borrow records
     */
    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    /**
     * Sets the borrow records.
     *
     * @param borrowRecords borrow records
     */
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }
}

/**
 * Represents a borrow record for a borrowed book.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    /**
     * Creates a borrow record with a borrowing date and a due date seven days later.
     *
     * @param borrowingDate borrowing date
     * @param book borrowed book
     */
    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate == null ? null : new Date(borrowingDate.getTime());
        this.book = book;
        this.returnDue = borrowingDate == null ? null : addDays(borrowingDate, 7);
    }

    /**
     * Extends the due date by seven days when today's date is before the current due date.
     *
     * @param today current date
     * @return the new due date if extended, otherwise the original due date
     */
    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return getReturnDue();
        }
        if (!today.before(returnDue)) {
            return getReturnDue();
        }
        returnDue = addDays(returnDue, 7);
        return getReturnDue();
    }

    /**
     * Checks whether the book is overdue.
     *
     * @param today current date
     * @return true if overdue, otherwise false
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
     * @return borrowing date
     */
    public Date getBorrowingDate() {
        return borrowingDate == null ? null : new Date(borrowingDate.getTime());
    }

    /**
     * Sets the borrowing date.
     *
     * @param borrowingDate borrowing date
     */
    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate == null ? null : new Date(borrowingDate.getTime());
    }

    /**
     * Gets the due date.
     *
     * @return due date
     */
    public Date getReturnDue() {
        return returnDue == null ? null : new Date(returnDue.getTime());
    }

    /**
     * Sets the due date.
     *
     * @param returnDue due date
     */
    public void setReturnDue(Date returnDue) {
        this.returnDue = returnDue == null ? null : new Date(returnDue.getTime());
    }

    /**
     * Gets the borrowed book.
     *
     * @return book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the borrowed book.
     *
     * @param book book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    private Date addDays(Date date, int days) {
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }
}

/**
 * Base class for all documents in the library.
 */
abstract class Document {
    private String title;

    /**
     * Creates a document.
     */
    public Document() {
    }

    /**
     * Gets the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title title
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
     * Creates a journal.
     */
    public Journal() {
    }

    /**
     * Gets the publication date.
     *
     * @return publication date
     */
    public Date getPublicationDate() {
        return publicationDate == null ? null : new Date(publicationDate.getTime());
    }

    /**
     * Sets the publication date.
     *
     * @param publicationDate publication date
     */
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate == null ? null : new Date(publicationDate.getTime());
    }
}

/**
 * Base class for volume documents.
 */
abstract class Volume extends Document {
    private String author;

    /**
     * Creates a volume.
     */
    public Volume() {
    }

    /**
     * Gets the author.
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     *
     * @param author author
     */
    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * Represents a book, the only borrowable document type.
 */
class Book extends Volume {
    private String bookId;

    /**
     * Creates a book.
     */
    public Book() {
    }

    /**
     * Gets the book ID.
     *
     * @return book ID
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets the book ID.
     *
     * @param bookId book ID
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
     * Creates a dictionary.
     */
    public Dictionary() {
    }

    /**
     * Gets the dictionary ID.
     *
     * @return dictionary ID
     */
    public String getDictionaryId() {
        return dictionaryId;
    }

    /**
     * Sets the dictionary ID.
     *
     * @param dictionaryId dictionary ID
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
     * Creates a comic.
     */
    public Comic() {
    }

    /**
     * Gets the recipient name.
     *
     * @return recipient name
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Sets the recipient name.
     *
     * @param recipientName recipient name
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}