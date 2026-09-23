public class BorrowRecord {
  private java.util.Date borrowingDate;
  private java.util.Date returnDue;
  private Book book;

  public BorrowRecord() {
  }

  public BorrowRecord(java.util.Date borrowingDate, Book book) {
    this.borrowingDate = borrowingDate;
    this.book = book;
  }

  public java.util.Date extendDueDate(java.util.Date today) {
    long sevenDays = 7L * 24L * 60L * 60L * 1000L;
    java.util.Date newDue = new java.util.Date(returnDue.getTime() + sevenDays);
    this.returnDue = newDue;
    return newDue;
  }

  public boolean isOverdue(java.util.Date today) {
    return today.getTime() > returnDue.getTime();
  }

  public java.util.Date getBorrowingDate() {
    return borrowingDate;
  }

  public void setBorrowingDate(java.util.Date borrowingDate) {
    this.borrowingDate = borrowingDate;
  }

  public java.util.Date getReturnDue() {
    return returnDue;
  }

  public void setReturnDue(java.util.Date returnDue) {
    this.returnDue = returnDue;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }
}
