import java.util.Objects;

public class Book extends Volume {
    private String bookId;

    public Book() {}

    public Book(String bookId, String title, String author) {
        super(title, author);
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(getTitle(), book.getTitle()) && Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), bookId);
    }
}
