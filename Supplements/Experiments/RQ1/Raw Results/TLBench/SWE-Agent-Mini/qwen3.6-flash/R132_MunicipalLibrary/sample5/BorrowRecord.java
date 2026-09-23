import java.util.Date;
import java.util.List;
import java.util.ArrayList;

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = new Date(borrowingDate.getTime() + 7 * 24 * 60 * 60 * 1000L);
    }

    public Date extendDueDate(Date today) {
        if (today != null && !today.before(borrowingDate) && today.before(returnDue)) {
            this.returnDue = new Date(returnDue.getTime() + 7 * 24 * 60 * 60 * 1000L);
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        return today != null && today.after(returnDue);
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
