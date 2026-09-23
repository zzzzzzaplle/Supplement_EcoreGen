import java.util.Objects;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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
}
