import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class Library {
    private String name;
    private Set<Member> members = new HashSet<>();
    private Set<Document> documents = new HashSet<>();

    public Library() {}

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
            return false;
        }
        for (Member m : members) {
            if (m.getFirstName().equals(firstName) && m.getSurname().equals(surname)) {
                return false;
            }
        }
        Member newMember = new Member();
        newMember.setFirstName(firstName);
        newMember.setSurname(surname);
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
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public Member() {}

    public boolean borrowBook(String bookTitle, Date today) {
        if (borrowRecords.size() >= 3) return false;
        
        Book target = null;
        for (Document doc : library.getDocuments()) {
            if (doc instanceof Book && doc.getTitle().equals(bookTitle)) {
                target = (Book) doc;
                break;
            }
        }
        
        if (target == null) return false;
        
        for (Member m : library.getMembers()) {
            for (BorrowRecord br : m.getBorrowRecords()) {
                if (br.getBook().getTitle().equals(bookTitle)) return false;
            }
        }
        
        BorrowRecord record = new BorrowRecord();
        record.setBook(target);
        record.setBorrowingDate(today);
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        record.setReturnDue(cal.getTime());
        
        borrowRecords.add(record);
        return true;
    }

    public boolean returnBook(String bookTitle, Date today) {
        for (Iterator<BorrowRecord> iterator = borrowRecords.iterator(); iterator.hasNext();) {
            BorrowRecord br = iterator.next();
            if (br.getBook().getTitle().equals(bookTitle)) {
                if (br.isOverdue(today)) return false;
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public Date extendReturnDueDate(String bookTitle, Date today) {
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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
}

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {}

    public Date extendDueDate(Date today) {
        if (!today.before(returnDue) || today.before(borrowingDate)) return returnDue;
        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDue);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        this.returnDue = cal.getTime();
        return this.returnDue;
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
    public Document() {}
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

abstract class Volume extends Document {
    private String author;
    public Volume() {}
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

class Book extends Volume {
    private String bookId;
    public Book() {}
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
}

class Dictionary extends Volume {
    private String dictionaryId;
    public Dictionary() {}
    public String getDictionaryId() { return dictionaryId; }
    public void setDictionaryId(String dictionaryId) { this.dictionaryId = dictionaryId; }
}

class Comic extends Volume {
    private String recipientName;
    public Comic() {}
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
}

class Journal extends Document {
    private Date publicationDate;
    public Journal() {}
    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
}