/**
 * Represents a failed action result.
 */
public class Failed extends ActionResult {

    private String reason;

    public Failed() {
        this("");
    }

    public Failed(String reason) {
        super(null);
        this.reason = reason;
    }

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
