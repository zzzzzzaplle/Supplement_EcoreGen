public class Document {
    private String title;

    public Document() {
    }

    protected Document(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Document)) {
            return false;
        }
        return this.title.equals(((Document) obj).getTitle());
    }
}
