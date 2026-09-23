import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        // Due date exactly seven days later
        this.returnDue = new Date(borrowingDate.getTime() + TimeUnit.DAYS.toMillis(7));
    }

    public Date extendDueDate(Date today) {
        // When today is on or after the borrowing date and strictly before the current due date
        if (!today.before(borrowingDate) && today.before(returnDue)) {
            // Move the due date forward by seven days
            this.returnDue = new Date(this.returnDue.getTime() + TimeUnit.DAYS.toMillis(7));
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        return today.after(returnDue);
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
