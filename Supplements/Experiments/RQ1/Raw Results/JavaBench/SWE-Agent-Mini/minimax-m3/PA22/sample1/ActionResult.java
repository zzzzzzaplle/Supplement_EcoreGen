public abstract class ActionResult {
    protected Action action;

    public ActionResult() {
    }

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
