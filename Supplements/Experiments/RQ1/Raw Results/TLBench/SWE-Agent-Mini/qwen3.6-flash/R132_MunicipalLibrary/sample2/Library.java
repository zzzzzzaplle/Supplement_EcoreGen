import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.util.Date;

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
        String fullName = firstName + " " + surname;
        for (Member existing : members) {
            if ((existing.getFirstName() + " " + existing.getSurname()).equals(fullName)) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        documents.add(doc);
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

    Book findBorrowableBookByTitle(String title) {
        for (Document doc : documents) {
            if (doc instanceof Book && doc.getTitle().equals(title)) {
                Book book = (Book) doc;
                if (!isBookBorrowedByAnyMember(book)) {
                    return book;
                }
            }
        }
        return null;
    }

    boolean isBookBorrowedByAnyMember(Book book) {
        for (Member member : members) {
            for (BorrowRecord record : member.getBorrowRecords()) {
                if (record.getBook() == book) {
                    return true;
                }
            }
        }
        return false;
    }
}
