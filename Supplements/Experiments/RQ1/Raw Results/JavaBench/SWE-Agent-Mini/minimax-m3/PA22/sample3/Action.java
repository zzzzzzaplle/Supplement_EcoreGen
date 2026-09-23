public abstract class Action {
    protected int initiator;

    protected Action() {
        this.initiator = 0;
    }

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return this.initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
