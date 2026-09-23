import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Represents a library that manages members and documents.
 */
class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.name = "";
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
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

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty() ||
            surname == null || surname.trim().isEmpty()) {
            return false;
        }

        String fullName = firstName.trim() + " " + surname.trim();

        for (Member member : this.members) {
            String existingName = member.getFirstName().trim() + " " + member.getSurname().trim();
            if (existingName.equals(fullName)) {
                return false;
            }
        }

        Member newMember = new Member(firstName, surname);
        newMember.setLibrary(this);
        this.members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            this.documents.add(doc);
        }
    }

    public Document findDocumentByTitle(String title) {
        if (title == null) {
            return null;
        }
        for (Document doc : this.documents) {
            if (doc.getTitle() != null && doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }
}

/**
 * Represents a registered member of the library.
 */
class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.firstName = "";
        this.surname = "";
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this();
        this.firstName = firstName;
        this.surname = surname;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
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

    public boolean borrowBook(String bookTitle, Date today) {
        if (this.library == null) {
            return false;
        }

        Document doc = this.library.findDocumentByTitle(bookTitle);
        if (doc == null) {
            return false;
        }

        if (!(doc instanceof Book)) {
            return false;
        }

        Book book = (Book) doc;

        // Check if the book is already borrowed by any member
        for (Member member : this.library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() == book) {
                    return false;
                }
            }
        }

        // Check member's current borrow count
        if (this.borrowRecords.size() >= 3) {
            return false;
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null) {
            return false;
        }

        // Find the borrow record for this book title
        BorrowRecord targetRecord = null;
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook() != null && 
                record.getBook().getTitle() != null && 
                record.getBook().getTitle().equals(bookTitle)) {
                targetRecord = record;
                break;
            }
        }

        if (targetRecord == null) {
            return false;
        }

        // Check if overdue
        if (targetRecord.isOverdue(today)) {
            return false;
        }

        // Remove the borrow record
        this.borrowRecords.remove(targetRecord);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null) {
            return null;
        }

        // Find the borrow record for this book title
        BorrowRecord targetRecord = null;
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook() != null && 
                record.getBook().getTitle() != null && 
                record.getBook().getTitle().equals(bookTitle)) {
                targetRecord = record;
                break;
            }
        }

        if (targetRecord == null) {
            return null;
        }

        return targetRecord.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook() != null && record.getBook().getTitle() != null) {
                titles.add(record.getBook().getTitle());
            }
        }
        return titles;
    }
}

/**
 * Represents a borrow record in the library system.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
        this.borrowingDate = null;
        this.returnDue = null;
        this.book = null;
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        // Set due date to 7 days later
        if (borrowingDate != null && book != null) {
            this.returnDue = new Date(borrowingDate.getTime() + TimeUnit.DAYS.toMillis(7));
        }
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

    /**
     * Extends the due date by 7 days if the request is valid.
     * Valid request: today is on or after borrowing date and strictly before current due date.
     */
    public Date extendDueDate(Date today) {
        if (today == null || this.borrowingDate == null || this.returnDue == null) {
            return this.returnDue;
        }

        // Check if today is on or after borrowing date
        if (today.before(this.borrowingDate) || today.getTime() == this.borrowingDate.getTime()) {
            // today is on or after borrowing date
        } else {
            // today is before borrowing date
            return this.returnDue;
        }

        // Check if today is strictly before current due date
        if (!today.before(this.returnDue)) {
            // today is on or after due date, cannot extend
            return this.returnDue;
        }

        // Extend due date by 7 days
        this.returnDue = new Date(this.returnDue.getTime() + TimeUnit.DAYS.toMillis(7));
        return this.returnDue;
    }

    /**
     * Checks if the book is overdue as of the given date.
     */
    public boolean isOverdue(Date today) {
        if (today == null || this.returnDue == null) {
            return false;
        }
        return today.after(this.returnDue);
    }
}

/**
 * Abstract base class for all documents in the library.
 */
abstract class Document {
    private String title;

    public Document() {
        this.title = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * A volume is a kind of document with an author.
 */
abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
        this.author = "";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * A book is a kind of volume with a unique bookId.
 */
class Book extends Volume {
    private String bookId;

    public Book() {
        super();
        this.bookId = "";
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

/**
 * A dictionary is a kind of volume with a dictionaryId.
 */
class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
        super();
        this.dictionaryId = "";
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

/**
 * A comic is a kind of volume with a recipient name.
 */
class Comic extends Volume {
    private String recipientName;

    public Comic() {
        super();
        this.recipientName = "";
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}

/**
 * A journal is a kind of document with a publication date.
 */
class Journal extends Document {
    private Date publicationDate;

    public Journal() {
        super();
        this.publicationDate = null;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}