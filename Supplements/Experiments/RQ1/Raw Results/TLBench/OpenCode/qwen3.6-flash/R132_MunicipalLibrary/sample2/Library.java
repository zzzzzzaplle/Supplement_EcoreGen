import java.util.*;

class Library {
    private String name;
    private Set<Member> members;
    private Set<Document> documents;

    public Library() {
        this.members = new LinkedHashSet<>();
        this.documents = new LinkedHashSet<>();
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

    public boolean registerMember(String firstName, String surname) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        if (surname == null || surname.trim().isEmpty()) {
            return false;
        }
        String fullName = firstName.trim() + " " + surname.trim();
        for (Member member : members) {
            String memberName = member.getFirstName().trim() + " " + member.getSurname().trim();
            if (fullName.equals(memberName)) {
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

    public Member findMember(String fullName) {
        for (Member m : members) {
            String mName = m.getFirstName().trim() + " " + m.getSurname().trim();
            if (fullName.equals(mName)) {
                return m;
            }
        }
        return null;
    }

    public Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (title.equals(doc.getTitle())) {
                return doc;
            }
        }
        return null;
    }

    public Map<String, Member> findAllMembersByTitle(String title) {
        Map<String, Member> result = new LinkedHashMap<>();
        for (Member m : members) {
            for (BorrowRecord record : m.getBorrowRecords()) {
                if (title.equals(record.getBook().getTitle())) {
                    result.put(m.getFirstName() + " " + m.getSurname(), m);
                }
            }
        }
        return result;
    }
}
