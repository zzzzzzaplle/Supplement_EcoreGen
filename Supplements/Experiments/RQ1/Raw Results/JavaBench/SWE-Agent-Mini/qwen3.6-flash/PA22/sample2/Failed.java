
/**
 * Action result indicating the action failed, with a reason.
 */
public class Failed extends ActionResult {
    private String reason;

    public Failed(String reason) {
        super(null);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
