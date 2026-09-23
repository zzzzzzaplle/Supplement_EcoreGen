public class InvalidInput extends Action {
    private String message;
    public InvalidInput() {}
    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
