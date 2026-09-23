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
        this.returnDue = addDays(borrowingDate, 7);
    }

    public Date extendDueDate(Date today) {
        if (!today.before(returnDue) || today.before(borrowingDate)) {
            return returnDue;
        }
        this.returnDue = addDays(returnDue, 7);
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        return today.after(returnDue);
    }

    private Date addDays(Date date, int days) {
        long millis = date.getTime();
        long newMillis = millis + TimeUnit.DAYS.toMillis(days);
        return new Date(newMillis);
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
