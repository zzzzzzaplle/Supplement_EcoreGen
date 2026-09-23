public class InvalidInput extends Action {
    private String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public InvalidInput() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
