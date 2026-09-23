abstract class ActionResult {

    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
