import java.util.Date;

public class Journal extends Document {
    private Date publicationDate;

    public Journal() {
        super();
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
