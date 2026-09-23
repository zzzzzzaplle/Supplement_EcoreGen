import java.util.*;
import java.text.SimpleDateFormat;

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
        this.returnDue = new Date(borrowingDate.getTime() + 7L * 24 * 60 * 60 * 1000);
    }

    public Date extendDueDate(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        String borrowStr = sdf.format(this.borrowingDate);
        String dueStr = sdf.format(this.returnDue);

        if (todayStr.compareTo(borrowStr) >= 0 && todayStr.compareTo(dueStr) < 0) {
            this.returnDue = new Date(today.getTime() + 7L * 24 * 60 * 60 * 1000);
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        String dueStr = sdf.format(this.returnDue);
        return todayStr.compareTo(dueStr) > 0;
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
        this.firstName = "";
        this.surname = "";
        this.borrowRecords = new ArrayList<>();
    }

    public Member(String firstName, String surname, Library library) {
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = library;
    }

    public Member(String firstName, String surname) {
        this();
        this.firstName = firstName;
        this.surname = surname;
        this.library = null;
    }

    public boolean borrowBook(String bookTitle, Date today) {
        if (this.borrowRecords.size() >= 3) {
            return false;
        }

        Book targetBook = null;
        if (this.library != null) {
            for (Document doc : this.library.getDocuments()) {
                if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                    targetBook = (Book) doc;
                    break;
                }
            }
        } else {
            return false;
        }

        if (targetBook == null) {
            return false;
        }

        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook().equals(targetBook)) {
                return false;
            }
        }

        for (Member member : this.library.getMembers()) {
            if (member == this) continue;
            for (BorrowRecord record : member.borrowRecords) {
                if (record.getBook().equals(targetBook)) {
                    return false;
                }
            }
        }

        BorrowRecord record = new BorrowRecord(today, targetBook);
        this.borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        BorrowRecord recordToRemove = null;
        for (BorrowRecord record : this.borrowRecords) {
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

        this.borrowRecords.remove(recordToRemove);
        return true;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
        for (BorrowRecord record : this.borrowRecords) {
            if (record.getBook().getTitle().equals(bookTitle)) {
                return record.extendDueDate(today);
            }
        }
        // If book not found, return null or original due date? 
        // Requirement says: "Otherwise, the due date remains unchanged and the original due date is returned."
        // But if the book isn't borrowed by this member, it's ambiguous. 
        // Assuming we only extend if we find the record. If not found, we can't return a due date from a non-existent record.
        // However, for safety, let's return null if not found, or throw exception? 
        // The prompt implies it works on "one of their own borrowed books". 
        // If not found, let's return null.
        return null;
    }

    public List<String> listBorrowedBookTitles() {
        List<String> titles = new ArrayList<>();
        for (BorrowRecord record : this.borrowRecords) {
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
    
    public Library getLibrary() {
        return library;
    }
    
    public void setLibrary(Library library) {
        this.library = library;
    }
}

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
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }

        String fullName = firstName.trim() + " " + surname.trim();
        for (Member member : this.members) {
            String memberFullName = member.getFirstName().trim() + " " + member.getSurname().trim();
            if (fullName.equals(memberFullName)) {
                return false;
            }
        }

        Member newMember = new Member(firstName.trim(), surname.trim(), this);
        this.members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        this.documents.add(doc);
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