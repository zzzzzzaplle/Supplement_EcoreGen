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
        if (this.members == null) {
            this.members = new HashSet<Member>();
        }
        for (Member m : this.members) {
            if (firstName.equals(m.getFirstName()) && surname.equals(m.getSurname())) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        this.members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (this.documents == null) {
            this.documents = new HashSet<Document>();
        }
        this.documents.add(doc);
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
