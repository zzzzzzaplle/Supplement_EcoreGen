import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public Library(String name) {
        this.name = name;
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
        for (Member m : members) {
            if (m.getFirstName().equals(firstName) && m.getSurname().equals(surname)) {
                return false;
            }
        }
        Member member = new Member(firstName, surname, this);
        members.add(member);
        return true;
    }

    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public boolean borrowBook(String bookTitle, String memberFirstName, String memberSurname, Date today) {
        Member member = findMember(memberFirstName, memberSurname);
        if (member == null) {
            return false;
        }
        return member.borrowBook(bookTitle, today);
    }

    public boolean returnBook(String bookTitle, String memberFirstName, String memberSurname, Date today) {
        Member member = findMember(memberFirstName, memberSurname);
        if (member == null) {
            return false;
        }
        return member.returnBook(bookTitle, today);
    }

    public Date extendReturnDueDate(String bookTitle, String memberFirstName, String memberSurname, Date today) {
        Member member = findMember(memberFirstName, memberSurname);
        if (member == null) {
            return null;
        }
        return member.extendReturnDueDate(bookTitle, today);
    }

    public List<String> listBorrowedBookTitles(String memberFirstName, String memberSurname) {
        Member member = findMember(memberFirstName, memberSurname);
        if (member == null) {
            return java.util.Collections.emptyList();
        }
        return member.listBorrowedBookTitles();
    }

    Document findDocumentByTitle(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equals(title)) {
                return doc;
            }
        }
        return null;
    }

    boolean isBookBorrowed(Book book) {
        for (Member member : members) {
            if (member.holdsBook(book)) {
                return true;
            }
        }
        return false;
    }

    private Member findMember(String firstName, String surname) {
        for (Member m : members) {
            if (m.getFirstName().equals(firstName) && m.getSurname().equals(surname)) {
                return m;
            }
        }
        return null;
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
