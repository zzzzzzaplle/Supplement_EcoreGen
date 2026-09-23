import java.util.Optional;

public class Failed extends ActionResult {
    private String reason;

    public Failed() {
        super();
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
