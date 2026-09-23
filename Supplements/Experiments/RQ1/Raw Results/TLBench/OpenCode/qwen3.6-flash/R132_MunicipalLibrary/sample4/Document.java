import java.util.*;

public abstract class Document {
    private String title;

    public Document() {}

    public Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Document other = (Document) obj;
        if (title == null) {
            return other.title == null;
        }
        return title.equals(other.title);
    }

    @Override
    public int hashCode() {
        return (title == null) ? 0 : title.hashCode();
    }
}
