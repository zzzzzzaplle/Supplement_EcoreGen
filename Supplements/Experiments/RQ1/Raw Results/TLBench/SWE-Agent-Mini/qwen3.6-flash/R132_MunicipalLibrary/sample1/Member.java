import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collections;
import java.util.Set;

public class Member {
  private Library library;
  private String firstName;
  private String surname;
  private List<BorrowRecord> borrowRecords;

  public Member() {
    this.borrowRecords = new ArrayList<BorrowRecord>();
  }

  public Member(String firstName, String surname, Library library) {
    this.firstName = firstName;
    this.surname = surname;
    this.library = library;
    this.borrowRecords = new ArrayList<BorrowRecord>();
  }

  public Member(String firstName, String surname) {
    this.firstName = firstName;
    this.surname = surname;
    this.borrowRecords = new ArrayList<BorrowRecord>();
  }

  private String getFullName() {
    return firstName + " " + surname;
  }

  public boolean borrowBook(String bookTitle, Date today) {
    if (library == null || bookTitle == null || today == null) {
      return false;
    }

    // Check if title exists and is a Book
    Book targetBook = null;
    for (Document doc : library.getDocuments()) {
      if (doc.getTitle().equalsIgnoreCase(bookTitle) && doc instanceof Book) {
        targetBook = (Book) doc;
        break;
      }
    }

    if (targetBook == null) {
      return false;
    }

    // Check if book is already borrowed by any member
    for (Member member : library.getMembers()) {
      for (BorrowRecord record : member.getBorrowRecords()) {
        if (record.getBook() != null && record.getBook().equals(targetBook)) {
          return false;
        }
      }
    }

    // Check member has fewer than 3 books
    if (borrowRecords.size() >= 3) {
      return false;
    }

    // Create borrow record
    BorrowRecord record = new BorrowRecord(today, targetBook);
    borrowRecords.add(record);
    return true;
  }

  public boolean returnBook(String bookTitle, Date today) {
    if (bookTitle == null || today == null) {
      return false;
    }

    // Find the borrow record
    BorrowRecord matchingRecord = null;
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook() != null && record.getBook().getTitle().equalsIgnoreCase(bookTitle)) {
        matchingRecord = record;
        break;
      }
    }

    if (matchingRecord == null) {
      return false;
    }

    // Check if overdue
    if (matchingRecord.isOverdue(today)) {
      return false;
    }

    // Remove the borrow record
    borrowRecords.remove(matchingRecord);
    return true;
  }

  public Date extendReturnDueDate(String bookTitle, Date today) {
    if (bookTitle == null || today == null) {
      return null;
    }

    // Find the borrow record
    BorrowRecord matchingRecord = null;
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook() != null && record.getBook().getTitle().equalsIgnoreCase(bookTitle)) {
        matchingRecord = record;
        break;
      }
    }

    if (matchingRecord == null) {
      return today == null ? null : today;
    }

    return matchingRecord.extendDueDate(today);
  }

  public List<String> listBorrowedBookTitles() {
    List<String> titles = new ArrayList<String>();
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook() != null) {
        titles.add(record.getBook().getTitle());
      }
    }
    return titles;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public List<BorrowRecord> getBorrowRecords() {
    return borrowRecords;
  }

  public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
    this.borrowRecords = borrowRecords;
  }

  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }
}
