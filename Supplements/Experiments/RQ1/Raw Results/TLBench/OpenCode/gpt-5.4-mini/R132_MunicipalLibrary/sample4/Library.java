import java.util.HashSet;
import java.util.List;
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
        if (firstName == null || surname == null || firstName.trim().isEmpty() || surname.trim().isEmpty()) {
            return false;
        }
        for (Member member : members) {
            if (member != null && firstName.equals(member.getFirstName()) && surname.equals(member.getSurname())) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        if (doc != null) {
            documents.add(doc);
        }
    }

    public boolean borrowBook(Member member, String bookTitle, java.util.Date today) {
        if (member == null || bookTitle == null || today == null) {
            return false;
        }
        Document target = null;
        for (Document document : documents) {
            if (document != null && bookTitle.equals(document.getTitle()) && document instanceof Book) {
                target = document;
                break;
            }
        }
        if (!(target instanceof Book)) {
            return false;
        }
        for (Member other : members) {
            if (other != null && other.getBorrowRecords() != null) {
                for (BorrowRecord record : other.getBorrowRecords()) {
                    if (record != null && record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                        return false;
                    }
                }
            }
        }
        if (member.getBorrowRecords() == null) {
            member.setBorrowRecords(new java.util.ArrayList<BorrowRecord>());
        }
        if (member.getBorrowRecords().size() >= 3) {
            return false;
        }
        BorrowRecord record = new BorrowRecord(today, (Book) target);
        member.getBorrowRecords().add(record);
        return true;
    }

    public boolean returnBook(Member member, String bookTitle, java.util.Date today) {
        if (member == null || bookTitle == null || today == null || member.getBorrowRecords() == null) {
            return false;
        }
        for (int i = 0; i < member.getBorrowRecords().size(); i++) {
            BorrowRecord record = member.getBorrowRecords().get(i);
            if (record != null && record.getBook() != null && bookTitle.equals(record.getBook().getTitle())) {
                if (record.isOverdue(today)) {
                    return false;
                }
                member.getBorrowRecords().remove(i);
                return true;
            }
        }
        return false;
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
