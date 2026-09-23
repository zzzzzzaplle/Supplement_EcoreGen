public class BorrowRecord {
    private java.util.Date borrowingDate;
    private java.util.Date returnDue;
    private Book book;

    public BorrowRecord() {
    }

    public BorrowRecord(java.util.Date borrowingDate, Book book) {
        this.borrowingDate = borrowingDate;
        this.book = book;
        if (borrowingDate != null) {
            this.returnDue = new java.util.Date(borrowingDate.getTime() + 7L * 24L * 60L * 60L * 1000L);
        }
    }

    public java.util.Date extendDueDate(java.util.Date today) {
        if (borrowingDate != null && returnDue != null && today != null && !today.before(borrowingDate) && today.before(returnDue)) {
            returnDue = new java.util.Date(returnDue.getTime() + 7L * 24L * 60L * 60L * 1000L);
        }
        return returnDue;
    }

    public boolean isOverdue(java.util.Date today) {
        return returnDue != null && today != null && today.after(returnDue);
    }

    public java.util.Date getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(java.util.Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public java.util.Date getReturnDue() {
        return returnDue;
    }

    public void setReturnDue(java.util.Date returnDue) {
        this.returnDue = returnDue;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
