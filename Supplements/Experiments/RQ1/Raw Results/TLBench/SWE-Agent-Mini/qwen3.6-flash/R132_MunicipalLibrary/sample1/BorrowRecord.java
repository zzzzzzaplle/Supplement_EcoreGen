import java.util.Date;

public class BorrowRecord {
  private Date borrowingDate;
  private Date returnDue;
  private Book book;

  public BorrowRecord() {
  }

  public BorrowRecord(Date borrowingDate, Book book) {
    this.borrowingDate = borrowingDate;
    this.book = book;
    // Calculate due date as 7 days after borrowing date
    long sevenDaysMillis = 7L * 24L * 60L * 60L * 1000L;
    this.returnDue = new Date(borrowingDate.getTime() + sevenDaysMillis);
  }

  public Date extendDueDate(Date today) {
    // When today is on or after the borrowing date and strictly before the current due date
    if (today != null && borrowingDate != null && returnDue != null) {
      if (today.getTime() >= borrowingDate.getTime() && today.getTime() < returnDue.getTime()) {
        // Extend due date by 7 days
        long sevenDaysMillis = 7L * 24L * 60L * 60L * 1000L;
        this.returnDue = new Date(this.returnDue.getTime() + sevenDaysMillis);
        return this.returnDue;
      }
    }
    return this.returnDue;
  }

  public boolean isOverdue(Date today) {
    if (today != null && returnDue != null) {
      return today.getTime() > returnDue.getTime();
    }
    return false;
  }

  public Date getBorrowingDate() {
    return borrowingDate;
  }

  public void setBorrowingDate(Date borrowingDate) {
    this.borrowingDate = borrowingDate;
  }

  public Date getReturnDue() {
    return returnDue;
  }

  public void setReturnDue(Date returnDue) {
    this.returnDue = returnDue;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }
}
