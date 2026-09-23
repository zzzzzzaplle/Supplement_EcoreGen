import java.util.Objects;

public abstract class Document {
    private String title;

    public Document() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(title, document.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
