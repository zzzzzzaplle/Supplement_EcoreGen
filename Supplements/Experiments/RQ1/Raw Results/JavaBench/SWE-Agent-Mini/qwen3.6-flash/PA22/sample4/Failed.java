class Failed extends ActionResult {
    private String reason;

    public Failed(String reason) {
        super(null);
        this.reason = reason;
    }

    public Failed() {
        super();
        this.reason = "";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
