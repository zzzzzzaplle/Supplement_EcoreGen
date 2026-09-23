import java.util.Date;
import java.text.SimpleDateFormat;

public class Journal extends Document {
    private Date publicationDate;

    public Journal() {}

    public Journal(String title, Date publicationDate) {
        super(title);
        this.publicationDate = publicationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
