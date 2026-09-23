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
        this.returnDue = addDays(borrowingDate, 7);
    }

    public Date extendDueDate(Date today) {
        if (today != null && borrowingDate != null && returnDue != null
                && (today.equals(borrowingDate) || today.after(borrowingDate))
                && today.before(returnDue)) {
            returnDue = addDays(returnDue, 7);
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.after(returnDue);
    }

    private static Date addDays(Date date, int days) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
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
