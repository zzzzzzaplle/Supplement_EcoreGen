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
        if (members == null) {
            members = new HashSet<Member>();
        }
        for (Member m : members) {
            if (m.getFirstName() != null && m.getFirstName().equals(firstName)
                    && m.getSurname() != null && m.getSurname().equals(surname)) {
                return false;
            }
        }
        Member newMember = new Member(firstName, surname, this);
        members.add(newMember);
        return true;
    }
    
    public void addDocument(Document doc) {
        if (documents == null) {
            documents = new HashSet<Document>();
        }
        documents.add(doc);
    }
    
    public Document findDocumentByTitle(String title) {
        if (documents == null || title == null) {
            return null;
        }
        for (Document d : documents) {
            if (title.equals(d.getTitle())) {
                return d;
            }
        }
        return null;
    }
    
    public boolean isBookBorrowed(Book book) {
        if (members == null) {
            return false;
        }
        for (Member m : members) {
            for (BorrowRecord r : m.getBorrowRecords()) {
                Book b = r.getBook();
                if (b == book || (b != null && book != null && b.getBookId() != null 
                        && b.getBookId().equals(book.getBookId()))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void registerBorrowRecord(BorrowRecord record) {
        // Tracking is done via members' borrowRecords
    }
    
    public void unregisterBorrowRecord(BorrowRecord record) {
        // Cleanup is done via members' borrowRecords
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
