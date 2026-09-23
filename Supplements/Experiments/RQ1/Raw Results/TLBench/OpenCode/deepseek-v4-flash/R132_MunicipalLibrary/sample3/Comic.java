public class Comic extends Volume {
    private String recipientName;

    public Comic() {
    }

    public Comic(String title, String author, String recipientName) {
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
