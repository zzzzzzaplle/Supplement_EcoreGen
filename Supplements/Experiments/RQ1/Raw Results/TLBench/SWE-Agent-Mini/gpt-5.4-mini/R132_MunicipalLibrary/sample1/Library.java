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
        if (firstName == null || surname == null || firstName.trim().isEmpty() || surname.trim().isEmpty()) {
            return false;
        }
        for (Member m : members) {
            if (m != null && firstName.equals(m.getFirstName()) && surname.equals(m.getSurname())) {
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

    public void setMembers(Set<Member> m) {
        this.members = m;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> d) {
        this.documents = d;
    }
}
