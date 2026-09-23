public abstract class Volume extends Document {
    private String author;

    public Volume() {
        super();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
