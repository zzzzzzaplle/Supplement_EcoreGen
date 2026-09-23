import java.time.LocalDate;

public class BorrowRecord {
    private LocalDate borrowingDate;
    private LocalDate returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(LocalDate borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = borrowingDate.plusDays(7);
    }

    public LocalDate extendDueDate(LocalDate today) {
        if (today != null && borrowingDate != null && returnDue != null) {
            if ((today.isEqual(borrowingDate) || today.isAfter(borrowingDate)) && today.isBefore(returnDue)) {
                returnDue = returnDue.plusDays(7);
                return returnDue;
            }
        }
        return returnDue;
    }

    public boolean isOverdue(LocalDate today) {
        if (today == null || returnDue == null) {
            return false;
        }
        return today.isAfter(returnDue);
    }

    public LocalDate getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDate getReturnDue() {
        return returnDue;
    }

    public void setReturnDue(LocalDate returnDue) {
        this.returnDue = returnDue;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
