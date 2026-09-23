import java.util.Date;

class BorrowRecord {
    private Date borrowingDate;
    private Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        this.returnDue = new Date(borrowingDate.getTime() + 7L * 24 * 60 * 60 * 1000);
    }

    public Date extendDueDate(Date today) {
        if (today.getTime() >= borrowingDate.getTime() && today.getTime() < returnDue.getTime()) {
            this.returnDue = new Date(returnDue.getTime() + 7L * 24 * 60 * 60 * 1000);
        }
        return this.returnDue;
    }

    public boolean isOverdue(Date today) {
        return today.getTime() > returnDue.getTime();
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
