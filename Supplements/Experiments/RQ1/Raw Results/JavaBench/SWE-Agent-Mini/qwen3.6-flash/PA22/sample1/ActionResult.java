public abstract class ActionResult {
    protected Action action;

    public ActionResult() {
    }

    protected ActionResult(Action action) {
        this.action = action;
    }
}
