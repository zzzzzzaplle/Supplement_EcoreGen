import java.util.Date;

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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        Document d = (Document) o;
        if (this.title == null) {
            return d.title == null;
        }
        return this.title.equals(d.title);
    }

    @Override
    public int hashCode() {
        return this.title == null ? 0 : this.title.hashCode();
    }
}
