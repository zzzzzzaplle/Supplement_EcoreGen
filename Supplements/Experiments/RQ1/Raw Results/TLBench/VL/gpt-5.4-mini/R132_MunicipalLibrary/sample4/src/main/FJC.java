import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base class for all documents in the library.
 */
abstract class Document {
    private String title;

    public Document() {
    }

    public Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * Abstract base class for volumes, which are documents with an author.
 */
abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
    }

    public Volume(String title, String author) {
        super(title);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * A book is a borrowable volume with a book identifier.
 */
class Book extends Volume {
    private String bookId;

    public Book() {
        super();
    }

    public Book(String title, String author, String bookId) {
        super(title, author);
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

/**
 * A dictionary is a volume with a dictionary identifier.
 */
class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
        super();
    }

    public Dictionary(String title, String author, String dictionaryId) {
        super(title, author);
        this.dictionaryId = dictionaryId;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

/**
 * A comic is a volume with a recipient name.
 */
class Comic extends Volume {
    private String recipientName;

    public Comic() {
        super();
    }

    public Comic(String title, String author, String recipientName) {
        super(title, author);
        this.recipientName = recipientName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}

/**
 * A journal is a document with a publication date.
 */
class Journal extends Document {
    private Date publicationDate;

    public Journal() {
        super();
    }

    public Journal(String title, Date publicationDate) {
        super(title);
        this.publicationDate = publicationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

/**
 * Represents a borrow record for a book.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = addDays(borrowingDate, 7);
    }

    public Date extendDueDate(Date today) {
        if (today != null && borrowingDate != null && returnDue != null
                && !today.before(borrowingDate) && today.before(returnDue)) {
            returnDue = addDays(returnDue, 7);
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        return today != null && returnDue != null && today.after(returnDue);
    }

    private Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
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
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname) {
        this(firstName, surname, null);
    }

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
        Document found = null;
        for (Document d : library.getDocuments()) {
            if (d != null && Objects.equals(bookTitle, d.getTitle())) {
                found = d;
                break;
            }
        }
        if (!(found instanceof Book)) {
            return false;
        }
        Book book = (Book) found;
        if (library.isBookBorrowed(book)) {
            return false;
        }
        borrowRecords.add(new BorrowRecord(today, book));
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return false;
        }
        for (Iterator<BorrowRecord> it = borrowRecords.iterator(); it.hasNext(); ) {
            BorrowRecord record = it.next();
            if (record != null && record.getBook() != null && Objects.equals(bookTitle, record.getBook().getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                it.remove();
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return null;
        }
        for (BorrowRecord record : borrowRecords) {
            if (record != null && record.getBook() != null && Objects.equals(bookTitle, record.getBook().getTitle())) {
                return record.extendDueDate(today);
            }
        }
        return null;
    }

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
 * Represents the municipal library.
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
        this.name = name;
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (isBlank(firstName) || isBlank(surname)) {
            return false;
        }
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member member : members) {
            if (member != null) {
                String existing = safeTrim(member.getFirstName()) + " " + safeTrim(member.getSurname());
                if (fullName.equals(existing)) {
                    return false;
                }
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    boolean isBookBorrowed(Book book) {
        if (book == null || members == null) {
            return false;
        }
        for (Member member : members) {
            if (member != null && member.getBorrowRecords() != null) {
                for (BorrowRecord record : member.getBorrowRecords()) {
                    if (record != null && record.getBook() != null && Objects.equals(record.getBook(), book)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}