import java.util.*;
import java.text.*;
import java.util.stream.*;

abstract class Document {
    private String title;

    public Document() {
        this.title = null;
    }

    public String getTitle() {
        return title;
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
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
        this.author = null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

class Book extends Volume {
    private String bookId;

    public Book() {
        super();
        this.bookId = null;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
        super();
        this.dictionaryId = null;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

class Comic extends Volume {
    private String recipientName;

    public Comic() {
        super();
        this.recipientName = null;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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
        this.borrowingDate = borrowingDate;
        this.book = book;
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowingDate);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        this.returnDue = cal.getTime();
    }

    public Date extendDueDate(Date today) {
        if (today != null && borrowingDate != null && returnDue != null) {
            if (!today.before(borrowingDate) && today.before(returnDue)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(returnDue);
                cal.add(Calendar.DAY_OF_YEAR, 7);
                this.returnDue = cal.getTime();
            }
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) return false;
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
}

class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.library = null;
        this.firstName = null;
        this.surname = null;
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
        this.library = null;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || today == null) return false;
        Set<Document> docs = library.getDocuments();
        if (docs == null) return false;

        // Find the book by title
        Document doc = docs.stream()
                .filter(d -> bookTitle.equals(d.getTitle()))
                .findFirst().orElse(null);
        if (!(doc instanceof Book)) return false;
        Book book = (Book) doc;

        // Check if book is already borrowed by any member
        boolean alreadyBorrowed = library.getMembers().stream()
                .flatMap(m -> m.getBorrowRecords().stream())
                .anyMatch(br -> br.getBook().equals(book));
        if (alreadyBorrowed) return false;

        // Check member has fewer than 3 books
        if (borrowRecords.size() >= 3) return false;

        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) return false;
        Optional<BorrowRecord> recordOpt = borrowRecords.stream()
                .filter(br -> bookTitle.equals(br.getBook().getTitle()))
                .findFirst();
        if (!recordOpt.isPresent()) return false;
        BorrowRecord record = recordOpt.get();
        if (record.isOverdue(today)) return false;
        borrowRecords.remove(record);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) return null;
        Optional<BorrowRecord> recordOpt = borrowRecords.stream()
                .filter(br -> bookTitle.equals(br.getBook().getTitle()))
                .findFirst();
        if (!recordOpt.isPresent()) return null;
        BorrowRecord record = recordOpt.get();
        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        return borrowRecords.stream()
                .map(br -> br.getBook().getTitle())
                .collect(Collectors.toList());
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

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.name = null;
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty() ||
            surname == null || surname.trim().isEmpty()) {
            return false;
        }
        // Check duplicate full name (case-insensitive)
        boolean exists = members.stream()
                .anyMatch(m -> m.getFirstName().equalsIgnoreCase(firstName) &&
                               m.getSurname().equalsIgnoreCase(surname));
        if (exists) return false;

        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }

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

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }
}