import java.util.Calendar;
import java.util.Date;

public class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {}

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowingDate);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        this.returnDue = cal.getTime();
    }

    public Date extendDueDate(Date today) {
        // "When today is on or after the borrowing date and strictly before the current due date"
        
        // Normalize dates to ignore hour/minute/second if necessary, but the task doesn't explicitly say.
        // Let's use simple comparisons.

        boolean onOrAfterBorrowing = !today.before(borrowingDate);
        boolean strictlyBeforeDue = today.before(returnDue);

        if (onOrAfterBorrowing && strictlyBeforeDue) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(returnDue);
            cal.add(Calendar.DAY_OF_YEAR, 7);
            returnDue = cal.getTime();
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        return today.after(returnDue);
    }

    public Date getBorrowingDate() { return borrowingDate; }
    public void setBorrowingDate(Date borrowingDate) { this.borrowingDate = borrowingDate; }
    public Date getReturnDue() { return returnDue; }
    public void setReturnDue(Date returnDue) { this.returnDue = returnDue; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}
