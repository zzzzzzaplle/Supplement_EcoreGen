import java.util.Date;
import java.text.SimpleDateFormat;

public class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {}

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date(borrowingDate.getTime());
        Date due = new Date(borrowingDate.getTime() + 7L * 24 * 60 * 60 * 1000);
        this.returnDue = due;
    }

    public Date extendDueDate(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        String borrowStr = sdf.format(this.borrowingDate);
        String dueStr = sdf.format(this.returnDue);
        if (todayStr.compareTo(borrowStr) >= 0 && todayStr.compareTo(dueStr) < 0) {
            this.returnDue = new Date(this.returnDue.getTime() + 7L * 24 * 60 * 60 * 1000);
            return this.returnDue;
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueStr = sdf.format(this.returnDue);
        String todayStr = sdf.format(today);
        return todayStr.compareTo(dueStr) > 0;
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
