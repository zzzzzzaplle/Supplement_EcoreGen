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
     * Creates an empty library with no name, members, or documents.
     */
    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (isBlank(firstName) || isBlank(surname)) {
            return false;
        }
        if (members == null) {
            members = new HashSet<>();
        }
        for (Member member : members) {
            if (member != null
                    && Objects.equals(member.getFirstName(), firstName)
                    && Objects.equals(member.getSurname(), surname)) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (documents == null) {
            documents = new HashSet<>();
        }
        if (doc != null) {
            documents.add(doc);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}

/**
 * Represents a registered member of the library.
 */
class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    /**
     * Creates a member with the given name and library.
     */
    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    /**
     * Creates a member without an associated library.
     */
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
        if (library.getDocuments() != null) {
            for (Document doc : library.getDocuments()) {
                if (doc != null && Objects.equals(doc.getTitle(), bookTitle)) {
                    found = doc;
                    break;
                }
            }
        }
        if (!(found instanceof Book)) {
            return false;
        }
        Book book = (Book) found;
        for (Member member : library.getMembers()) {
            if (member != null && member.getBorrowRecords() != null) {
                for (BorrowRecord record : member.getBorrowRecords()) {
                    if (record != null && record.getBook() != null
                            && Objects.equals(record.getBook().getTitle(), bookTitle)) {
                        return false;
                    }
                }
            }
        }
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null || borrowRecords == null) {
            return false;
        }
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record != null && record.getBook() != null
                    && Objects.equals(record.getBook().getTitle(), bookTitle)) {
                if (record.isOverdue(today)) {
                    return false;
                }
                borrowRecords.remove(i);
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
            if (record != null && record.getBook() != null
                    && Objects.equals(record.getBook().getTitle(), bookTitle)) {
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
            if (record != null && record.getBook() != null) {
                titles.add(record.getBook().getTitle());
            }
        }
        return titles;
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
}

/**
 * Represents a borrow record for a borrowed book.
 */
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    /**
     * Creates a borrow record with a 7-day due date.
     */
    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate == null ? null : new Date(borrowingDate.getTime());
        this.book = book;
        if (borrowingDate == null) {
            this.returnDue = null;
        } else {
            this.returnDue = new Date(borrowingDate.getTime() + TimeUnit.DAYS.toMillis(7));
        }
    }

    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return returnDue;
        }
        if (!today.before(returnDue)) {
            return returnDue;
        }
        long newDue = returnDue.getTime() + TimeUnit.DAYS.toMillis(7);
        returnDue = new Date(newDue);
        return new Date(returnDue.getTime());
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.after(returnDue);
    }

    public Date getBorrowingDate() {
        return borrowingDate == null ? null : new Date(borrowingDate.getTime());
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate == null ? null : new Date(borrowingDate.getTime());
    }

    public Date getReturnDue() {
        return returnDue == null ? null : new Date(returnDue.getTime());
    }

    public void setReturnDue(Date returnDue) {
        this.returnDue = returnDue == null ? null : new Date(returnDue.getTime());
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}

/**
 * Base abstract type for all documents in the library.
 */
abstract class Document {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * A journal is a document with a publication date.
 */
class Journal extends Document {
    private Date publicationDate;

    public Journal() {
    }

    public Date getPublicationDate() {
        return publicationDate == null ? null : new Date(publicationDate.getTime());
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate == null ? null : new Date(publicationDate.getTime());
    }
}

/**
 * Base abstract type for volume-like documents.
 */
abstract class Volume extends Document {
    private String author;

    public Volume() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

/**
 * A book is a volume that can be borrowed.
 */
class Book extends Volume {
    private String bookId;

    public Book() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

/**
 * A dictionary is a volume with a catalog entry identifier.
 */
class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
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
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}