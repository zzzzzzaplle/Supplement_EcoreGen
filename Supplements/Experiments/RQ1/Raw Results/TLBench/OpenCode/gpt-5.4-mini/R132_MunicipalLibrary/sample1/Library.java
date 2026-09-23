import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
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
        if (!isBlank(firstName) && !isBlank(surname)) {
            String fullName = firstName.trim() + " " + surname.trim();
            for (Member member : members) {
                String memberFullName = member.getFirstName() + " " + member.getSurname();
                if (fullName.equals(memberFullName)) {
                    return false;
                }
            }
            Member member = new Member(firstName.trim(), surname.trim(), this);
            members.add(member);
            return true;
        }
        return false;
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
