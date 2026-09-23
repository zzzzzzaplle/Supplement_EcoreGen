public abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public Action() {
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
