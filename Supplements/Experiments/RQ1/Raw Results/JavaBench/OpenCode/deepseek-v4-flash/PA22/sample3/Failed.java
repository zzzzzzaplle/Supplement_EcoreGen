public class Failed extends ActionResult {
    private String reason;

    public Failed() {
    }

    public Failed(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
