
/**
 * Abstract base class for action results.
 */
public abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }
}
