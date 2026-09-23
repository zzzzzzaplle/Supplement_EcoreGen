import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new HashSet<>();
        this.documents = new HashSet<>();
    }

    public boolean registerMember(String firstName, String surname) {
        // Validate inputs
        if (firstName == null || firstName.trim().isEmpty()) return false;
        if (surname == null || surname.trim().isEmpty()) return false;

        String trimmedFirst = firstName.trim();
        String trimmedSurname = surname.trim();

        // Check for duplicate full name
        for (Member member : members) {
            if (member.getFirstName().equals(trimmedFirst) &&
                member.getSurname().equals(trimmedSurname)) {
                return false;
            }
        }

        Member newMember = new Member(trimmedFirst, trimmedSurname, this);
        members.add(newMember);
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
