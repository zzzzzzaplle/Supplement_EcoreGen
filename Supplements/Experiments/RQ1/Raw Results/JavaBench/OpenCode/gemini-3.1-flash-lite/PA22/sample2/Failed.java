public class Failed extends ActionResult {
    private String reason;

    public Failed() {}
    public Failed(Action action, String reason) { super(action); this.reason = reason; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
