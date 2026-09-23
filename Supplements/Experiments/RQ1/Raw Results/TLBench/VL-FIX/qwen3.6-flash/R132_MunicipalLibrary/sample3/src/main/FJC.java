import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.name = "";
        this.members = new HashSet<Member>();
        this.documents = new HashSet<Document>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Member> getMembers() {
        return this.members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
            return false;
        }

        for (Member member : this.members) {
            if (member.getFirstName().equals(firstName) && member.getSurname().equals(surname)) {
                return false;
            }
        }

        Member newMember = new Member(firstName, surname, this);
        this.members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            this.documents.add(doc);
        }
    }

    public Document findDocumentByTitle(String title) {
        for (Document doc : this.documents) {
            if (doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }

    public Member findMemberByFullName(String firstName, String surname) {
        for (Member member : this.members) {
            if (member.getFirstName().equals(firstName) && member.getSurname().equals(surname)) {
                return member;
            }
        }
        return null;
    }
}

class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.library = null;
        this.firstName = "";
        this.surname = "";
        this.borrowRecords = new ArrayList<BorrowRecord>();
    }

    public Member(String firstName, String surname) {
        this();
        this.firstName = firstName;
        this.surname = surname;
    }

    public Member(String firstName, String surname, Library library) {
        this(firstName, surname);
        this.library = library;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return this.borrowRecords;
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

        // Check if book is already borrowed by any member
        for (Member member : this.library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook().equals(book)) {
                    return false;
                }
            }
        }

        // Check if member already has 3 books
        if (this.borrowRecords.size() >= 3) {
            return false;
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (this.library == null) {
            return false;
        }

        Document doc = this.library.findDocumentByTitle(bookTitle);
        if (doc == null || !(doc instanceof Book)) {
            return false;
        }

        Book book = (Book) doc;

        // Find the borrow record for this book
        BorrowRecord recordToRemove = null;
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook().equals(book)) {
                recordToRemove = record;
                break;
            }
        }

        if (recordToRemove == null) {
            return false;
        }

        // Check if overdue
        if (recordToRemove.isOverdue(today)) {
            return false;
        }

        // Remove the record
        this.borrowRecords.remove(recordToRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (this.library == null) {
            return null;
        }

        Document doc = this.library.findDocumentByTitle(bookTitle);
        if (doc == null || !(doc instanceof Book)) {
            return null;
        }

        Book book = (Book) doc;

        // Find the borrow record for this book
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook().equals(book)) {
                return record.extendDueDate(today);
            }
        }

        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<String>();
        for (BorrowRecord record : this.borrowRecords) {
            titles.add(record.getBook().getTitle());
        }
        return titles;
    }
}

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
        this();
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = addDays(borrowingDate, 7);
    }

    private Date addDays(Date date, int days) {
        long millisInDay = 24 * 60 * 60 * 1000;
        return new Date(date.getTime() + days * millisInDay);
    }

    public Date getBorrowingDate() {
        return this.borrowingDate;
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public Date getReturnDue() {
        return this.returnDue;
    }

    public void setReturnDue(Date returnDue) {
        this.returnDue = returnDue;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date extendDueDate(Date today) {
        // When today is on or after the borrowing date and strictly before the current due date
        if (today != null && this.borrowingDate != null && this.returnDue != null) {
            if (!today.before(this.borrowingDate) && today.before(this.returnDue)) {
                this.returnDue = addDays(this.returnDue, 7);
                return this.returnDue;
            }
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today != null && this.returnDue != null) {
            return today.after(this.returnDue);
        }
        return false;
    }
}

abstract class Document {
    private String title;

    public Document() {
        this.title = "";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

class Journal extends Document {
    private Date publicationDate;

    public Journal() {
        super();
        this.publicationDate = null;
    }

    public Date getPublicationDate() {
        return this.publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
        this.author = "";
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

class Book extends Volume {
    private String bookId;

    public Book() {
        super();
        this.bookId = UUID.randomUUID().toString();
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
        super();
        this.dictionaryId = UUID.randomUUID().toString();
    }

    public String getDictionaryId() {
        return this.dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

class Comic extends Volume {
    private String recipientName;

    public Comic() {
        super();
        this.recipientName = "";
    }

    public String getRecipientName() {
        return this.recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}