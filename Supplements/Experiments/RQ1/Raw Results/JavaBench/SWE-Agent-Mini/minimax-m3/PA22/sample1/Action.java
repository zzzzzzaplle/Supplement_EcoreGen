public abstract class Action {
    protected int initiator;

    public Action() {
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
