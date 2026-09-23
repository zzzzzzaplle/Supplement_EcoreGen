class InvalidInput extends Action {

    private String message;

    public InvalidInput() {
        this(0, "");
    }

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
