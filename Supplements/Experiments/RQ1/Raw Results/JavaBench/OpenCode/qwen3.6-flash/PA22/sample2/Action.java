/**
 * Abstract base class for all actions.
 */
public abstract class Action {

    private int initiator;

    public Action() {
        this(0);
    }

    public Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
