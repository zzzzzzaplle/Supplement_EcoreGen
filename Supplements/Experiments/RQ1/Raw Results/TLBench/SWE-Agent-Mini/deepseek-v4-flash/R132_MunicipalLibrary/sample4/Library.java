import java.util.Set;
import java.util.HashSet;

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
