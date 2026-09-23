public class Failed extends ActionResult {
    private String reason;

    public Failed(Action action) {
        super(action);
    }

    public Failed() {
        super(null);
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
