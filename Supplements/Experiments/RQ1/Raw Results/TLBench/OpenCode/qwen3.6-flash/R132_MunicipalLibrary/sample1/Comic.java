public class Comic extends Volume {
    private String recipientName;

    public Comic() {}

    public Comic(String recipientName, String title, String author) {
        super(title, author);
        this.recipientName = recipientName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
