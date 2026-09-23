import java.util.*;

public class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        members = new LinkedHashSet<>();
        documents = new LinkedHashSet<>();
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

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }
        for (Member member : members) {
            if (member.getFirstName().equals(firstName) && member.getSurname().equals(surname)) {
                return false;
            }
        }
        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    public boolean isTitleTaken(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }

    public Book findBookByTitle(String title) {
        Document doc = findDocumentByTitle(title);
        if (doc instanceof Book) {
            return (Book) doc;
        }
        return null;
    }
}
