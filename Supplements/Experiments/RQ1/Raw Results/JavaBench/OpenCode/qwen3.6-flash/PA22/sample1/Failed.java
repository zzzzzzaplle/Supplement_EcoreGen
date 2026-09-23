class Failed extends ActionResult {

    private String reason;

    public Failed(String reason) {
        super(null);
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
