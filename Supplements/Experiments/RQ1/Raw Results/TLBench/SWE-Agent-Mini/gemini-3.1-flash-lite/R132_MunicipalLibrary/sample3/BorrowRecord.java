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
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowingDate);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        this.returnDue = cal.getTime();
    }

    public Date extendDueDate(Date today) {
        // Requirement: today >= borrowingDate AND today < returnDue
        if ((today.after(borrowingDate) || today.equals(borrowingDate)) && today.before(returnDue)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(returnDue);
            cal.add(Calendar.DAY_OF_YEAR, 7);
            this.returnDue = cal.getTime();
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
