public abstract class ActionResult {
    private Action action;

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
