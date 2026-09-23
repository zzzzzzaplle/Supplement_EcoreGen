import java.text.SimpleDateFormat;
import java.util.*;

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
        if (firstName == null || firstName.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
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

    public Member(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = null;
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
        this.borrowRecords = new ArrayList<>();
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        if (borrowRecords.size() >= 3) {
            return false;
        }
        Book book = null;
        if (library != null) {
            for (Document doc : library.getDocuments()) {
                if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                    book = (Book) doc;
                    break;
                }
            }
        }
        if (book == null) {
            return false;
        }
        if (library != null) {
            for (Member m : library.getMembers()) {
                for (BorrowRecord br : m.getBorrowRecords()) {
                    if (br.getBook().equals(book)) {
                        return false;
                    }
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        Date borrowingDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date dueDate = cal.getTime();
        BorrowRecord record = new BorrowRecord(borrowingDate, book);
        record.setReturnDue(dueDate);
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            return false;
        }
        BorrowRecord toReturn = null;
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                toReturn = br;
                break;
            }
        }
        if (toReturn == null) {
            return false;
        }
        if (toReturn.isOverdue(today)) {
            return false;
        }
        borrowRecords.remove(toReturn);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        if (bookTitle == null || today == null) {
            for (BorrowRecord br : borrowRecords) {
                if (br.getBook().getTitle().equals(bookTitle)) {
                    return br.getReturnDue();
                }
            }
            return null;
        }
        for (BorrowRecord br : borrowRecords) {
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (!today.before(br.getBorrowingDate()) && today.before(br.getReturnDue())) {
                    return br.extendDueDate(today);
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

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
        this.borrowingDate = new Date();
        this.returnDue = new Date();
        this.book = new Book();
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowingDate);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        this.returnDue = cal.getTime();
    }

    public Date extendDueDate(Date today) {
        if (today != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.returnDue);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            this.returnDue = cal.getTime();
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
        this.publicationDate = new Date();
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