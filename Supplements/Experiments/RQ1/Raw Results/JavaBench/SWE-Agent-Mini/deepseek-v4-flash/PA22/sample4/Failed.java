public class Failed extends ActionResult {
    private String reason;

    public Failed(Action action) {
        super(action);
    }

    public Failed() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
