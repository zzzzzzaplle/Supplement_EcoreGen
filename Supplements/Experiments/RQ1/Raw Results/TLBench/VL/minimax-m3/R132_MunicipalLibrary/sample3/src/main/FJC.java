import java.util.*;
import java.text.SimpleDateFormat;

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty() ||
            surname == null || surname.trim().isEmpty()) {
            return false;
        }
        for (Member m : members) {
            if (m.getFirstName().equals(firstName) && m.getSurname().equals(surname)) {
                return false;
            }
        }
        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Member> getMembers() { return members; }
    public void setMembers(Set<Member> members) { this.members = members; }
    public Set<Document> getDocuments() { return documents; }
    public void setDocuments(Set<Document> documents) { this.documents = documents; }
}

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
        this.firstName = firstName;
        this.surname = surname;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Book bookToBorrow = null;
        for (Document doc : library.getDocuments()) {
            if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                bookToBorrow = (Book) doc;
                break;
            }
        }
        if (bookToBorrow == null) {
            return false;
        }
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().equals(bookToBorrow)) {
                    return false;
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        Date borrowingDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date dueDate = cal.getTime();
        BorrowRecord record = new BorrowRecord(borrowingDate, bookToBorrow);
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord recordToReturn = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                recordToReturn = br;
                break;
            }
        }
        if (recordToReturn == null) {
            return false;
        }
        if (recordToReturn.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(recordToReturn);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (!today.before(br.getBorrowingDate()) && today.before(br.getReturnDue())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(br.getReturnDue());
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    Date newDueDate = cal.getTime();
                    br.setReturnDue(newDueDate);
                    return newDueDate;
                } else {
                    return br.getReturnDue();
                }
            }
        }
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            titles.add(br.getBook().getTitle());
        }
        return titles;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }
}

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
    }

    public Date extendDueDate(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDue);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date newDueDate = cal.getTime();
        this.returnDue = newDueDate;
        return newDueDate;
    }

    public boolean isOverdue(Date today) {
        return today.after(returnDue);
    }

    public Date getBorrowingDate() { return borrowingDate; }
    public void setBorrowingDate(Date borrowingDate) { this.borrowingDate = borrowingDate; }
    public Date getReturnDue() { return returnDue; }
    public void setReturnDue(Date returnDue) { this.returnDue = returnDue; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}

abstract class Document {
    private String title;

    public Document() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

class Journal extends Document {
    private Date publicationDate;

    public Journal() {
    }

    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
}

abstract class Volume extends Document {
    private String author;

    public Volume() {
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

class Book extends Volume {
    private String bookId;

    public Book() {
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
}

class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
    }

    public String getDictionaryId() { return dictionaryId; }
    public void setDictionaryId(String dictionaryId) { this.dictionaryId = dictionaryId; }
}

class Comic extends Volume {
    private String recipientName;

    public Comic() {
    }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
}