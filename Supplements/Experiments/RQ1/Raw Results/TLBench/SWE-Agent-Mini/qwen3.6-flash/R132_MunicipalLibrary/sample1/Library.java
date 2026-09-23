import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Library {
  private String name;
  private Set<Member> members;
  private Set<Document> documents;

  public Library() {
    this.members = new HashSet<Member>();
    this.documents = new HashSet<Document>();
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
    // Validate inputs
    if (firstName == null || firstName.trim().isEmpty() ||
        surname == null || surname.trim().isEmpty()) {
      return false;
    }

    String fullName = firstName.trim() + " " + surname.trim();

    // Check for duplicate members
    for (Member member : members) {
      if (member.getFirstName() != null && member.getSurname() != null) {
        String existingName = member.getFirstName().trim() + " " + member.getSurname().trim();
        if (existingName.equals(fullName)) {
          return false;
        }
      }
    }

    // Create new member with reference to this library
    Member newMember = new Member(firstName.trim(), surname.trim(), this);
    members.add(newMember);
    return true;
  }

  public void addDocument(Document doc) {
    if (doc != null) {
      documents.add(doc);
    }
  }
}
