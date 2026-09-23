public class Journal extends Document {
    private String publicationDate;

    public Journal() {
    }

    public Journal(String title, String publicationDate) {
        super(title);
        this.publicationDate = publicationDate;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }
}
