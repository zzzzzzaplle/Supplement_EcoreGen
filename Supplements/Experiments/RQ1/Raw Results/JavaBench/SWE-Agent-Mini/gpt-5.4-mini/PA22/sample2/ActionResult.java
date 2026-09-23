public abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public ActionResult() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
