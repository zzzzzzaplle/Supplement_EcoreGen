import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

// ---------- Date utility class ----------
class DateUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    static {
        sdf.setLenient(false);
    }

    public static Date parse(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + dateStr + ". Expected format: " + DATE_FORMAT);
        }
    }

    public static String format(Date date) {
        return sdf.format(date);
    }

    public static Date today() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static boolean isBefore(Date d1, Date d2) {
        return d1.before(d2);
    }

    public static boolean isAfterOrEqual(Date d1, Date d2) {
        return !d1.before(d2);
    }
}

// ---------- Abstract Document ----------
abstract class Document {
    private String title;

    public Document() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

// ---------- Journal ----------
class Journal extends Document {
    private Date publicationDate;

    public Journal() {}

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}

// ---------- Abstract Volume ----------
abstract class Volume extends Document {
    private String author;

    public Volume() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

// ---------- Book ----------
class Book extends Volume {
    private String bookId;

    public Book() {}

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

// ---------- Dictionary ----------
class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {}

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

// ---------- Comic ----------
class Comic extends Volume {
    private String recipientName;

    public Comic() {}

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}

// ---------- BorrowRecord ----------
class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {}

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = DateUtil.addDays(borrowingDate, 7);
    }

    public Date extendDueDate(Date today) {
        if ((today.equals(borrowingDate) || today.after(borrowingDate)) && today.before(returnDue)) {
            returnDue = DateUtil.addDays(returnDue, 7);
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

// ---------- Member ----------
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
        // find the book document in library
        Document doc = library.findDocumentByTitle(bookTitle);
        if (doc == null) return false;
        if (!(doc instanceof Book)) return false;
        Book book = (Book) doc;

        // check if book is already borrowed by any member
        if (library.isBookBorrowed(book)) return false;

        // check member's current borrow count
        if (borrowRecords.size() >= 3) return false;

        // create borrow record
        BorrowRecord record = new BorrowRecord(today, book);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) return false;
        if (record.isOverdue(today)) return false;
        borrowRecords.remove(record);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        BorrowRecord record = findBorrowRecordByTitle(bookTitle);
        if (record == null) return null;
        return record.extendDueDate(today);
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            titles.add(record.getBook().getTitle());
        }
        return titles;
    }

    private BorrowRecord findBorrowRecordByTitle(String title) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getTitle().equals(title)) {
                return record;
            }
        }
        return null;
    }

    // Getters and setters
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

// ---------- Library ----------
class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty()) return false;
        if (surname == null || surname.trim().isEmpty()) return false;

        // Check for duplicate full name
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

    public Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }

    public boolean isBookBorrowed(Book book) {
        for (Member member : members) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook().equals(book)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Getters and setters
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