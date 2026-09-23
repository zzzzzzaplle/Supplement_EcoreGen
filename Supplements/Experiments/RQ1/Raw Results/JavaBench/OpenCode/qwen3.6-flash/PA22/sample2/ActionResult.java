/**
 * Abstract base class for action results.
 */
public abstract class ActionResult {

    private Action action;

    public ActionResult() {
    }

    public ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
