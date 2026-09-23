public class Book extends Volume {
  private String bookId;

  public Book() {
  }

  public Book(String title, String author, String bookId) {
    super(title, author);
    this.bookId = bookId;
  }

  public String getBookId() {
    return bookId;
  }

  public void setBookId(String bookId) {
    this.bookId = bookId;
  }
}
