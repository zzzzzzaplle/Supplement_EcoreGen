import java.util.*;

public class Library {
    private String name;
    private Set<Member> members = new LinkedHashSet<>();
    private Set<Document> documents = new LinkedHashSet<>();

    public Library() {}

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

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }
        String fullName = firstName + " " + surname;
        for (Member m : members) {
            if ((m.getFirstName() + " " + m.getSurname()).equals(fullName)) {
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
}
