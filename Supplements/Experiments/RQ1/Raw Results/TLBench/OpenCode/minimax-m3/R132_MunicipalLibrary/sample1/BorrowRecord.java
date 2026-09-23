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
            long sevenDaysInMillis = 7L * 24L * 60L * 60L * 1000L;
            this.returnDue = new Date(borrowingDate.getTime() + sevenDaysInMillis);
        }
    }

    public Date extendDueDate(Date today) {
        if (this.borrowingDate == null || this.returnDue == null || today == null) {
            return this.returnDue;
        }
        boolean onOrAfterBorrowing = !today.before(this.borrowingDate);
        boolean strictlyBeforeDue = today.before(this.returnDue);
        if (onOrAfterBorrowing && strictlyBeforeDue) {
            long sevenDaysInMillis = 7L * 24L * 60L * 60L * 1000L;
            this.returnDue = new Date(this.returnDue.getTime() + sevenDaysInMillis);
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        if (this.returnDue == null || today == null) {
            return false;
        }
        return today.after(this.returnDue);
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
