import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

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

        for (Member member : members) {
            if (member.getFirstName().equals(firstName) && member.getSurname().equals(surname)) {
                return false;
            }
        }

        Member newMember = new Member(firstName, surname);
        newMember.setLibrary(this);
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

    public Member() {
        this.firstName = "";
        this.surname = "";
        this.borrowRecords = new ArrayList<>();
        this.library = null;
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
        this.borrowRecords = new ArrayList<>();
        this.library = null;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (library == null || bookTitle == null || bookTitle.isEmpty()) {
            return false;
        }

        Book targetBook = null;
        Document foundDoc = null;
        for (Document doc : library.getDocuments()) {
            if (doc.getTitle().equals(bookTitle)) {
                foundDoc = doc;
                if (doc instanceof Book) {
                    targetBook = (Book) doc;
                }
                break;
            }
        }

        if (targetBook == null) {
            return false;
        }

        // Check if already borrowed by any member
        for (Member member : library.getMembers()) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() == targetBook) {
                    return false;
                }
            }
        }

        // Check if member already has 3 books
        if (borrowRecords.size() >= 3) {
            return false;
        }

        Date dueDate = new Date(today.getTime() + 7L * 24 * 60 * 60 * 1000);
        BorrowRecord record = new BorrowRecord(today, targetBook);
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || bookTitle.isEmpty()) {
            return false;
        }

        int indexToRemove = -1;
        for (int i = 0; i < borrowRecords.size(); i++) {
            BorrowRecord record = borrowRecords.get(i);
            if (record.getBook().getTitle().equals(bookTitle)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return false;
        }

        BorrowRecord record = borrowRecords.get(indexToRemove);
        if (record.isOverdue(today)) {
            return false;
        }

        borrowRecords.remove(indexToRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || bookTitle.isEmpty()) {
            return null;
        }

        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                Date currentDue = record.getReturnDue();
                
                // Check if today is on or after borrowing date and strictly before due date
                if (!today.before(record.getBorrowingDate()) && today.before(currentDue)) {
                    // Move due date forward by 7 days
                    Date newDue = new Date(currentDue.getTime() + 7L * 24 * 60 * 60 * 1000);
                    record.setReturnDue(newDue);
                    return newDue;
                }
                return currentDue;
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
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
        this.returnDue = null;
    }

    public Date extendDueDate(Date today) {
        if (today != null) {
            Date currentDue = this.returnDue;
            if (!today.before(this.borrowingDate) && today.before(currentDue)) {
                Date newDue = new Date(currentDue.getTime() + 7L * 24 * 60 * 60 * 1000);
                this.returnDue = newDue;
                return newDue;
            }
            return currentDue;
        }
        return this.returnDue;
    }

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
}

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
        this.bookId = "";
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
        this.dictionaryId = "";
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