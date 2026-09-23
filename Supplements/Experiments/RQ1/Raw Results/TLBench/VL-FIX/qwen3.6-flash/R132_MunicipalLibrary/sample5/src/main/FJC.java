import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }
        
        String fullName = firstName.trim() + " " + surname.trim();
        
        for (Member member : members) {
            String existingFullName = member.getFirstName().trim() + " " + member.getSurname().trim();
            if (existingFullName.equals(fullName)) {
                return false;
            }
        }
        
        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }
}

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
        this.firstName = firstName;
        this.surname = surname;
        this.library = null;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null) {
            return false;
        }

        Document targetDoc = null;
        for (Document doc : library.getDocuments()) {
            if (doc.getTitle().equals(bookTitle)) {
                targetDoc = doc;
                break;
            }
        }

        if (targetDoc == null) {
            return false;
        }

        if (!(targetDoc instanceof Book)) {
            return false;
        }

        Book book = (Book) targetDoc;

        // Check if book is already borrowed by anyone
        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook().equals(book)) {
                    return false;
                }
            }
        }

        // Check if member already has 3 books
        if (borrowRecords.size() >= 3) {
            return false;
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord recordToRemove = null;
        
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                recordToRemove = record;
                break;
            }
        }

        if (recordToRemove == null) {
            return false;
        }

        if (recordToRemove.isOverdue(today)) {
            return false;
        }

        borrowRecords.remove(recordToRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record.extendDueDate(today);
            }
        }
        // If book not found, return null or original? 
        // Requirement implies extending one of their own borrowed books.
        // If not found, behavior is undefined, but typically returns null or original.
        // Let's assume if not found, no change. But method returns Date.
        // If record not found, we can't return a due date. Let's return null.
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
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
}

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        // Set due date to 7 days later
        this.returnDue = new Date(borrowingDate.getTime() + (7L * 24 * 60 * 60 * 1000));
    }

    public Date extendDueDate(Date today) {
        // When today is on or after the borrowing date and strictly before the current due date
        if (!today.before(borrowingDate) && today.before(returnDue)) {
            this.returnDue = new Date(returnDue.getTime() + (7L * 24 * 60 * 60 * 1000));
            return returnDue;
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
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

abstract class Document {
    private String title;

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
        this.author = "";
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
        this.bookId = UUID.randomUUID().toString();
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
        this.dictionaryId = UUID.randomUUID().toString();
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
        this.recipientName = "";
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}