import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
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

    public Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (doc.getTitle() != null && doc.getTitle().equals(title)) {
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
