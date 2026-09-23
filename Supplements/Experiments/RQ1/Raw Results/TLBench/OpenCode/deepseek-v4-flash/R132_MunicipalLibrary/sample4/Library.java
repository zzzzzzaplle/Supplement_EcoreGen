import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
        if (firstName == null || surname == null
                || firstName.trim().isEmpty() || surname.trim().isEmpty()) {
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

    public Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }

    public boolean isBookBorrowedByAnyMember(String bookTitle) {
        for (Member m : members) {
            for (BorrowRecord record : m.getBorrowRecords()) {
                if (record.getBook().getTitle().equals(bookTitle)) {
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
