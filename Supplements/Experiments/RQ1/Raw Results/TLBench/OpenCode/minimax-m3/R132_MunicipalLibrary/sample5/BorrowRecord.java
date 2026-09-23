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
            Calendar cal = Calendar.getInstance();
            cal.setTime(borrowingDate);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            this.returnDue = cal.getTime();
        }
    }

    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return returnDue;
        }
        if (today.compareTo(borrowingDate) >= 0 && today.compareTo(returnDue) < 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(returnDue);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            returnDue = cal.getTime();
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.compareTo(returnDue) > 0;
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
