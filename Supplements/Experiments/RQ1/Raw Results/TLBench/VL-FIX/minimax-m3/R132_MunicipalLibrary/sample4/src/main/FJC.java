import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.name = "";
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
        Member member = new Member(firstName, surname, this);
        members.add(member);
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

class Member {
    private Library library;
    private String firstName;
    private String surname;
    private List<BorrowRecord> borrowRecords;

    public Member() {
        this.library = null;
        this.firstName = "";
        this.surname = "";
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
        if (library == null || bookTitle == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                return false;
            }
        }
        for (Document d : library.getDocuments()) {
            if (d.getTitle().equals(bookTitle) && d instanceof Book) {
                Book book = (Book) d;
                for (Member m : library.getMembers()) {
                    for (BorrowRecord rec : m.getBorrowRecords()) {
                        if (rec.getBook().getBookId().equals(book.getBookId())) {
                            return false;
                        }
                    }
                }
                Date dueDate = addDays(today, 7);
                BorrowRecord record = new BorrowRecord(today, book);
                record.setReturnDue(dueDate);
                borrowRecords.add(record);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null) {
            return false;
        }
        for (Iterator<BorrowRecord> it = borrowRecords.iterator(); it.hasNext();) {
            BorrowRecord br = it.next();
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) {
                    return false;
                }
                it.remove();
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null) {
            return null;
        }
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                return br.extendDueDate(today);
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

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
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
    }

    public Date extendDueDate(Date today) {
        if (today == null) {
            return returnDue;
        }
        if (!today.before(borrowingDate)) {
            if (today.before(returnDue)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(returnDue);
                cal.add(Calendar.DATE, 7);
                this.returnDue = cal.getTime();
            }
        }
        return returnDue;
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