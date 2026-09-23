public class Member {
  private Library library;
  private String firstName;
  private String surname;
  private java.util.List<BorrowRecord> borrowRecords;

  public Member() {
    this.borrowRecords = new java.util.ArrayList<>();
  }

  public Member(String firstName, String surname, Library library) {
    this.library = library;
    this.firstName = firstName;
    this.surname = surname;
    this.borrowRecords = new java.util.ArrayList<>();
  }

  public Member(String firstName, String surname) {
    this.firstName = firstName;
    this.surname = surname;
    this.borrowRecords = new java.util.ArrayList<>();
  }

  public boolean borrowBook(String bookTitle, java.util.Date today) {
    if (library == null) {
      return false;
    }
    if (borrowRecords.size() >= 3) {
      return false;
    }
    if (!library.isDocumentRegistered(bookTitle)) {
      return false;
    }
    Book book = library.getBookByTitle(bookTitle);
    if (book == null) {
      return false;
    }
    if (isBookAlreadyBorrowed(bookTitle)) {
      return false;
    }
    long sevenDays = 7L * 24L * 60L * 60L * 1000L;
    java.util.Date dueDate = new java.util.Date(today.getTime() + sevenDays);
    BorrowRecord record = new BorrowRecord(today, book);
    record.setReturnDue(dueDate);
    borrowRecords.add(record);
    return true;
  }

  public boolean isBookAlreadyBorrowed(String bookTitle) {
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook().getTitle().trim().equals(bookTitle.trim())) {
        return true;
      }
    }
    for (Member m : library.getMembers()) {
      if (m == this) {
        continue;
      }
      for (BorrowRecord record : m.borrowRecords) {
        if (record.getBook().getTitle().trim().equals(bookTitle.trim())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean returnBook(String bookTitle, java.util.Date today) {
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook().getTitle().trim().equals(bookTitle.trim())) {
        if (record.isOverdue(today)) {
          return false;
        }
        borrowRecords.remove(record);
        return true;
      }
    }
    return false;
  }

  public java.util.Date extendReturnDueDate(String bookTitle, java.util.Date today) {
    for (BorrowRecord record : borrowRecords) {
      if (record.getBook().getTitle().trim().equals(bookTitle.trim())) {
        if (today.getTime() >= record.getBorrowingDate().getTime() && today.getTime() < record.getReturnDue().getTime()) {
          java.util.Date newDue = record.extendDueDate(today);
          return newDue;
        }
        return record.getReturnDue();
      }
    }
    return null;
  }

  public java.util.List<String> listBorrowedBookTitles() {
    java.util.List<String> titles = new java.util.ArrayList<>();
    for (BorrowRecord record : borrowRecords) {
      titles.add(record.getBook().getTitle());
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

  public java.util.List<BorrowRecord> getBorrowRecords() {
    return borrowRecords;
  }

  public void setBorrowRecords(java.util.List<BorrowRecord> borrowRecords) {
    this.borrowRecords = borrowRecords;
  }
}
