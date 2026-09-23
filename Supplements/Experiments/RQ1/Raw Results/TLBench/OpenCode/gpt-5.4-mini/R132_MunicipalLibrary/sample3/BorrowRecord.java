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
        this.returnDue = new Date(borrowingDate.getTime() + 7L * 24L * 60L * 60L * 1000L);
    }

    public Date extendDueDate(Date today) {
        if (today == null || borrowingDate == null || returnDue == null) {
            return returnDue;
        }
        if (!today.before(returnDue)) {
            return returnDue;
        }
        if (today.before(borrowingDate)) {
            return returnDue;
        }
        returnDue = new Date(returnDue.getTime() + 7L * 24L * 60L * 60L * 1000L);
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
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
