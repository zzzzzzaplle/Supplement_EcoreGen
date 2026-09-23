import java.util.*;

public class BorrowRecord {
    private java.util.Date borrowingDate;
    private java.util.Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(java.util.Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = this.addDays(borrowingDate, 7);
    }

    public void extendDueDate(java.util.Date today) {
        this.returnDue = this.addDays(this.returnDue, 7);
    }

    public boolean isOverdue(java.util.Date today) {
        return today.after(this.returnDue);
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

    private java.util.Date addDays(java.util.Date date, int days) {
        long millisperday = days * 24L * 60L * 60L * 1000L;
        return new java.util.Date(date.getTime() + millisperday);
    }
}
