import java.util.*;
import java.text.SimpleDateFormat;

public class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowingDate);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        this.returnDue = cal.getTime();
    }

    public Date extendDueDate(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        String borrowingStr = sdf.format(borrowingDate);
        String dueStr = sdf.format(returnDue);

        if (todayStr.compareTo(borrowingStr) >= 0 && todayStr.compareTo(dueStr) < 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(returnDue);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            this.returnDue = cal.getTime();
            return returnDue;
        }
        return returnDue;
    }

    public boolean isOverdue(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        String dueStr = sdf.format(returnDue);
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
