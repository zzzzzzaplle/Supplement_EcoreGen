import java.util.*;

 class LibraryManagementSystem {

    // ===================== Library =====================
    public static class Library {
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
            for (Member m : members) {
                if (m.getFirstName().equalsIgnoreCase(firstName.trim())
                        && m.getSurname().equalsIgnoreCase(surname.trim())) {
                    return false;
                }
            }
            Member newMember = new Member(firstName.trim(), surname.trim(), this);
            members.add(newMember);
            return true;
        }

        public void addDocument(Document doc) {
            if (doc != null) {
                documents.add(doc);
            }
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Set<Member> getMembers() { return members; }
        public void setMembers(Set<Member> members) { this.members = members; }
        public Set<Document> getDocuments() { return documents; }
        public void setDocuments(Set<Document> documents) { this.documents = documents; }
    }

    // ===================== Member =====================
    public static class Member {
        private Library library;
        private String firstName;
        private String surname;
        private List<BorrowRecord> borrowRecords;

        public Member() {
            this.borrowRecords = new ArrayList<>();
        }

        public Member(String firstName, String surname) {
            this.firstName = firstName;
            this.surname = surname;
            this.borrowRecords = new ArrayList<>();
        }

        public Member(String firstName, String surname, Library library) {
            this.firstName = firstName;
            this.surname = surname;
            this.library = library;
            this.borrowRecords = new ArrayList<>();
        }

        public boolean borrowBook(String bookTitle, Date today) {
            if (library == null || bookTitle == null) return false;
            if (borrowRecords.size() >= 3) return false;

            Document doc = null;
            for (Document d : library.getDocuments()) {
                if (d.getTitle() != null && d.getTitle().equals(bookTitle)) {
                    doc = d;
                    break;
                }
            }
            if (doc == null) return false;
            if (!(doc instanceof Book)) return false;
            Book book = (Book) doc;

            // Check if book is already borrowed
            for (Member m : library.getMembers()) {
                for (BorrowRecord br : m.getBorrowRecords()) {
                    if (br.getBook() != null && br.getBook().equals(book)) {
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

        public boolean returnBook(String bookTitle, Date today) {
            if (bookTitle == null) return false;
            BorrowRecord target = null;
            for (BorrowRecord br : borrowRecords) {
                if (br.getBook() != null && br.getBook().getTitle() != null
                        && br.getBook().getTitle().equals(bookTitle)) {
                    target = br;
                    break;
                }
            }
            if (target == null) return false;
            if (target.isOverdue(today)) return false;
            borrowRecords.remove(target);
            return true;
        }

        public Date extendReturnDueDate(String bookTitle, Date today) {
            if (bookTitle == null) return null;
            for (BorrowRecord br : borrowRecords) {
                if (br.getBook() != null && br.getBook().getTitle() != null
                        && br.getBook().getTitle().equals(bookTitle)) {
                    return br.extendDueDate(today);
                }
            }
            return null;
        }

        public List<String> listBorrowedBookTitles() {
            List<String> titles = new ArrayList<>();
            for (BorrowRecord br : borrowRecords) {
                if (br.getBook() != null && br.getBook().getTitle() != null) {
                    titles.add(br.getBook().getTitle());
                }
            }
            return titles;
        }

        private Date addDays(Date date, int days) {
            if (date == null) return null;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, days);
            return cal.getTime();
        }

        public Library getLibrary() { return library; }
        public void setLibrary(Library library) { this.library = library; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
        public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    }

    // ===================== BorrowRecord =====================
    public static class BorrowRecord {
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
            if (today == null) return returnDue;
            if (borrowingDate == null) return returnDue;
            if (returnDue == null) return returnDue;

            if (!today.before(borrowingDate)) {
                if (today.before(returnDue)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(returnDue);
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    returnDue = cal.getTime();
                }
            }
            return returnDue;
        }

        public boolean isOverdue(Date today) {
            if (today == null || returnDue == null) return false;
            return today.after(returnDue);
        }

        public Date getBorrowingDate() { return borrowingDate; }
        public void setBorrowingDate(Date borrowingDate) { this.borrowingDate = borrowingDate; }
        public Date getReturnDue() { return returnDue; }
        public void setReturnDue(Date returnDue) { this.returnDue = returnDue; }
        public Book getBook() { return book; }
        public void setBook(Book book) { this.book = book; }
    }

    // ===================== Document =====================
    public abstract static class Document {
        private String title;

        public Document() {
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    // ===================== Journal =====================
    public static class Journal extends Document {
        private Date publicationDate;

        public Journal() {
        }

        public Date getPublicationDate() { return publicationDate; }
        public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    }

    // ===================== Volume =====================
    public abstract static class Volume extends Document {
        private String author;

        public Volume() {
        }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
    }

    // ===================== Book =====================
    public static class Book extends Volume {
        private String bookId;

        public Book() {
        }

        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
    }

    // ===================== Dictionary =====================
    public static class Dictionary extends Volume {
        private String dictionaryId;

        public Dictionary() {
        }

        public String getDictionaryId() { return dictionaryId; }
        public void setDictionaryId(String dictionaryId) { this.dictionaryId = dictionaryId; }
    }

    // ===================== Comic =====================
    public static class Comic extends Volume {
        private String recipientName;

        public Comic() {
        }

        public String getRecipientName() { return recipientName; }
        public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    }
}