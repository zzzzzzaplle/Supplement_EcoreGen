import java.util.HashSet;
import java.util.Set;

public class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        members = new HashSet<Member>();
        documents = new HashSet<Document>();
    }

    public boolean registerMember(String firstName, String surname) {
        if (isBlank(firstName) || isBlank(surname)) return false;
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member m : members) {
            if (m != null && fullName.equals(m.getFirstName().trim() + " " + m.getSurname().trim())) return false;
        }
        Member member = new Member(firstName.trim(), surname.trim(), this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) documents.add(doc);
    }

    public Document findDocumentByTitle(String title) {
        if (title == null) return null;
        for (Document d : documents) if (d != null && title.equals(d.getTitle())) return d;
        return null;
    }

    public boolean isBookBorrowed(Book book) {
        if (book == null) return false;
        for (Member m : members) {
            if (m != null && m.getBorrowRecords() != null) {
                for (BorrowRecord r : m.getBorrowRecords()) if (r != null && r.getBook() == book) return true;
            }
        }
        return false;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Member> getMembers() { return members; }
    public void setMembers(Set<Member> m) { this.members = m; }
    public Set<Document> getDocuments() { return documents; }
    public void setDocuments(Set<Document> d) { this.documents = d; }
}
