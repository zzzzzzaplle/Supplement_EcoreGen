public abstract class ActionResult {
    protected Action action;

    public ActionResult() {
        this.action = null;
    }

    protected ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
