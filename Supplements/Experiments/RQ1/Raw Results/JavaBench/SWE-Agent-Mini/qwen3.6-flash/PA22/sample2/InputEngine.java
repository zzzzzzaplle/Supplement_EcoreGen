
/**
 * Interface for input engines.
 */
public interface InputEngine {
    /**
     * Fetch an action from the user.
     * @return the action
     */
    Action fetchAction();
}
