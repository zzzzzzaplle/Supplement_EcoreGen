import java.util.Date;
import java.util.Calendar;

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        long msInDay = 24L * 60 * 60 * 1000;
        this.returnDue = new Date(borrowingDate.getTime() + 7 * msInDay);
    }

    public Date extendDueDate(Date today) {
        Calendar calToday = Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);

        Calendar calBorrowing = Calendar.getInstance();
        calBorrowing.setTime(this.borrowingDate);
        calBorrowing.set(Calendar.HOUR_OF_DAY, 0);
        calBorrowing.set(Calendar.MINUTE, 0);
        calBorrowing.set(Calendar.SECOND, 0);
        calBorrowing.set(Calendar.MILLISECOND, 0);

        Calendar calReturnDue = Calendar.getInstance();
        calReturnDue.setTime(this.returnDue);
        calReturnDue.set(Calendar.HOUR_OF_DAY, 0);
        calReturnDue.set(Calendar.MINUTE, 0);
        calReturnDue.set(Calendar.SECOND, 0);
        calReturnDue.set(Calendar.MILLISECOND, 0);

        if (!calToday.before(calBorrowing) && calToday.before(calReturnDue)) {
            long msInDay = 24L * 60 * 60 * 1000;
            this.returnDue = new Date(this.returnDue.getTime() + 7 * msInDay);
            return this.returnDue;
        } else {
            return this.returnDue;
        }
    }

    public boolean isOverdue(Date today) {
        Calendar calToday = Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);

        Calendar calReturnDue = Calendar.getInstance();
        calReturnDue.setTime(this.returnDue);
        calReturnDue.set(Calendar.HOUR_OF_DAY, 0);
        calReturnDue.set(Calendar.MINUTE, 0);
        calReturnDue.set(Calendar.SECOND, 0);
        calReturnDue.set(Calendar.MILLISECOND, 0);

        return calToday.after(calReturnDue);
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
