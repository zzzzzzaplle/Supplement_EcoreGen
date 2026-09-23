public abstract class ActionResult {
    private Action action;

    public ActionResult(Action action) {
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
