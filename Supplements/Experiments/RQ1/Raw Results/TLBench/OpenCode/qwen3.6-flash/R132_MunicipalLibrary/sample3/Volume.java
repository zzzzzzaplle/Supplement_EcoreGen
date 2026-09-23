public class Volume extends Document {
    private String author;

    public Volume() {
    }

    protected Volume(String title) {
        super(title);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
