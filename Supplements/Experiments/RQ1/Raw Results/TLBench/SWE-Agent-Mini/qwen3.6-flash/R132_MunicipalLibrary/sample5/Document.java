import java.util.List;
import java.util.ArrayList;

abstract class Document {
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
