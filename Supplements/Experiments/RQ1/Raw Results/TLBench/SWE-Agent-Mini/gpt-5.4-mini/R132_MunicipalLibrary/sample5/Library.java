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
        if (isBlank(firstName) || isBlank(surname)) {
            return false;
        }
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member m : members) {
            if (m != null && fullName.equals(m.getFirstName() + " " + m.getSurname())) {
                return false;
            }
        }
        Member member = new Member(firstName.trim(), surname.trim(), this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
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
