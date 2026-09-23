public class Library {
    private String name;
    private java.util.Set<Member> members;
    private java.util.Set<Document> documents;

    public Library() {
        this.members = new java.util.HashSet<Member>();
        this.documents = new java.util.HashSet<Document>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (isBlank(firstName) || isBlank(surname)) {
            return false;
        }
        String fullName = firstName + " " + surname;
        for (Member member : members) {
            if (member != null && fullName.equals(member.getFirstName() + " " + member.getSurname())) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (documents == null) {
            documents = new java.util.HashSet<Document>();
        }
        documents.add(doc);
    }

    public boolean borrowBook(Member member, String bookTitle, java.util.Date today) {
        if (member == null || isBlank(bookTitle) || today == null || member.getBorrowRecords() == null || member.getBorrowRecords().size() >= 3) {
            return false;
        }
        Book book = findAvailableBook(bookTitle);
        if (book == null) {
            return false;
        }
        BorrowRecord borrowRecord = new BorrowRecord(today, book);
        member.getBorrowRecords().add(borrowRecord);
        return true;
    }

    public java.util.Date extendReturnDueDate(Member member, String bookTitle, java.util.Date today) {
        BorrowRecord record = findMemberBorrowRecord(member, bookTitle);
        if (record == null || today == null) {
            return record == null ? null : record.getReturnDue();
        }
        return record.extendDueDate(today);
    }

    public boolean returnBook(Member member, String bookTitle, java.util.Date today) {
        BorrowRecord record = findMemberBorrowRecord(member, bookTitle);
        if (record == null || record.isOverdue(today)) {
            return false;
        }
        return member.getBorrowRecords().remove(record);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Set<Member> getMembers() {
        return members;
    }

    public void setMembers(java.util.Set<Member> members) {
        this.members = members;
    }

    public java.util.Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(java.util.Set<Document> documents) {
        this.documents = documents;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Book findAvailableBook(String bookTitle) {
        if (documents == null) {
            return null;
        }
        for (Document document : documents) {
            if (document instanceof Book && bookTitle.equals(document.getTitle()) && !isBorrowed((Book) document)) {
                return (Book) document;
            }
        }
        return null;
    }

    private boolean isBorrowed(Book book) {
        for (Member member : members) {
            if (member != null && member.getBorrowRecords() != null) {
                for (BorrowRecord borrowRecord : member.getBorrowRecords()) {
                    if (borrowRecord != null && borrowRecord.getBook() == book) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private BorrowRecord findMemberBorrowRecord(Member member, String bookTitle) {
        if (member == null || member.getBorrowRecords() == null || bookTitle == null) {
            return null;
        }
        for (BorrowRecord borrowRecord : member.getBorrowRecords()) {
            if (borrowRecord != null && borrowRecord.getBook() != null && bookTitle.equals(borrowRecord.getBook().getTitle())) {
                return borrowRecord;
            }
        }
        return null;
    }
}
