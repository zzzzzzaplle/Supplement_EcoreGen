public class Library {
  private String name;
  private java.util.Set<Member> members;
  private java.util.Set<Document> documents;

  public Library() {
    this.members = new java.util.HashSet<>();
    this.documents = new java.util.HashSet<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public java.util.Set<Member> getMembers() {
    return members;
  }

  public void setMembers(java.util.Set<Member> members) {
    this.members = members;
  }

  public java.util.Set<Document> getDocuments() {
    return documents;
  }

  public void setDocuments(java.util.Set<Document> documents) {
    this.documents = documents;
  }

  public boolean registerMember(String firstName, String surname) {
    if (firstName == null || firstName.trim().isEmpty()) {
      return false;
    }
    if (surname == null || surname.trim().isEmpty()) {
      return false;
    }
    String fullName = firstName.trim() + " " + surname.trim();
    for (Member m : members) {
      if (m.getFirstName().trim().equals(firstName.trim()) && m.getSurname().trim().equals(surname.trim())) {
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

  boolean isDocumentRegistered(String title) {
    for (Document doc : documents) {
      if (doc.getTitle().trim().equals(title.trim())) {
        return true;
      }
    }
    return false;
  }

  private Document getDocumentByTitle(String title) {
    for (Document doc : documents) {
      if (doc.getTitle().trim().equals(title.trim())) {
        return doc;
      }
    }
    return null;
  }

  Book getBookByTitle(String title) {
    Document doc = getDocumentByTitle(title);
    if (doc instanceof Book) {
      return (Book) doc;
    }
    return null;
  }
}
