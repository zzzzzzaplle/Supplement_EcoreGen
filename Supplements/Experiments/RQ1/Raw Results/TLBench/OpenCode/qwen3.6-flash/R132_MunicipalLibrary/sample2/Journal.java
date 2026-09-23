import java.util.*;

class Journal extends Document {
    private Date publicationDate;

    public Journal() {
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
