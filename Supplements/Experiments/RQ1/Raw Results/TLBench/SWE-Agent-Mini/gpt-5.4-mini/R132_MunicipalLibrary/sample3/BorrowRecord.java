import java.util.Calendar;
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
        if (borrowingDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(borrowingDate);
            c.add(Calendar.DAY_OF_MONTH, 7);
            this.returnDue = c.getTime();
        }
    }

    public Date extendDueDate(Date today) {
        if (borrowingDate == null || returnDue == null || today == null) return returnDue;
        if (!today.before(returnDue) || today.before(borrowingDate)) return returnDue;
        Calendar c = Calendar.getInstance();
        c.setTime(returnDue);
        c.add(Calendar.DAY_OF_MONTH, 7);
        returnDue = c.getTime();
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) return false;
        return today.after(returnDue);
    }

    public Date getBorrowingDate() { return borrowingDate; }
    public void setBorrowingDate(Date borrowingDate) { this.borrowingDate = borrowingDate; }
    public Date getReturnDue() { return returnDue; }
    public void setReturnDue(Date returnDue) { this.returnDue = returnDue; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}
