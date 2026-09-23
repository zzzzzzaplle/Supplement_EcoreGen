import java.util.Date;
import java.util.Calendar;

public class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowingDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        this.returnDue = calendar.getTime();
    }

    public Date extendDueDate(Date today) {
        if (!today.after(returnDue) && !today.equals(returnDue)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(returnDue);
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            this.returnDue = calendar.getTime();
        }
        return returnDue;
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
