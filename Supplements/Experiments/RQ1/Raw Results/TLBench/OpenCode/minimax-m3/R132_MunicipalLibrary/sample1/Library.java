import java.util.HashSet;
import java.util.Set;

public class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new HashSet<Member>();
        this.documents = new HashSet<Document>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }
        for (Member m : this.members) {
            if (m.getFirstName() != null && m.getSurname() != null
                    && m.getFirstName().equals(firstName)
                    && m.getSurname().equals(surname)) {
                return false;
            }
        }
        Member newMember = new Member(firstName, surname, this);
        this.members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc == null) {
            return;
        }
        for (Document existing : this.documents) {
            if (existing.getTitle() != null && existing.getTitle().equals(doc.getTitle())) {
                return;
            }
        }
        this.documents.add(doc);
    }

    public Document findDocumentByTitle(String title) {
        if (title == null) {
            return null;
        }
        for (Document d : this.documents) {
            if (title.equals(d.getTitle())) {
                return d;
            }
        }
        return null;
    }

    public boolean isBookBorrowed(Book book) {
        if (book == null) {
            return false;
        }
        for (Member m : this.members) {
            for (BorrowRecord r : m.getBorrowRecords()) {
                if (r.getBook() != null && r.getBook().getTitle() != null
                        && r.getBook().getTitle().equals(book.getTitle())) {
                    return true;
                }
            }
        }
        return false;
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
