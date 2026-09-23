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
        // First name and surname must not be null, empty, or whitespace-only
        if (firstName == null || firstName.trim().isEmpty() ||
            surname == null || surname.trim().isEmpty()) {
            return false;
        }
        // Reject when another member already has exactly the same full name
        for (Member member : members) {
            if (member.getFirstName().equals(firstName) && member.getSurname().equals(surname)) {
                return false;
            }
        }
        // Create the member with no borrowed books
        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (documents == null) {
            documents = new HashSet<>();
        }
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
}
