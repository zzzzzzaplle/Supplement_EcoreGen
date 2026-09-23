import java.time.LocalDate;

public class Journal extends Document {
    private LocalDate publicationDate;

    public Journal() {
        super();
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
}
