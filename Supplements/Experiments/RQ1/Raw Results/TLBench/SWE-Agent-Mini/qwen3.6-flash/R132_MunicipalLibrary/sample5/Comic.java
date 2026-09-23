import java.util.List;
import java.util.ArrayList;

class Comic extends Volume {
    private String recipientName;

    public Comic() {
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
