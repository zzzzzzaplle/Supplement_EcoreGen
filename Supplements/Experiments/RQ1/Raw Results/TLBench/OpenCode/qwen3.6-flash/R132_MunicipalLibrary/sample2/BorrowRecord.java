import java.util.*;

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
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

    public Date extendDueDate(Date today) {
        if (today.before(borrowingDate) || !today.before(returnDue)) {
            return returnDue;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDue);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date newDue = cal.getTime();
        returnDue = newDue;
        return newDue;
    }

    public boolean isOverdue(Date today) {
        return today.after(returnDue);
    }
}
