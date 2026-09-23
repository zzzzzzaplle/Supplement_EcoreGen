
/**
 * Action representing invalid user input.
 */
public class InvalidInput extends Action {
    private String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
