import java.util.Objects;

public abstract class Document {
    private String title;

    public Document() {
    }

    public Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
