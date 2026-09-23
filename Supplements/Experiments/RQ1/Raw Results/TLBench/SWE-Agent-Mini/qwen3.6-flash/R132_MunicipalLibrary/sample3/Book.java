import java.util.ArrayList;
import java.util.List;

public class Book extends Volume {
    private String bookId;
    private String title;
    private Boolean borrowed;

    public Book() {
        this.title = "";
        this.borrowed = false;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Boolean borrowed) {
        this.borrowed = borrowed;
    }
}
