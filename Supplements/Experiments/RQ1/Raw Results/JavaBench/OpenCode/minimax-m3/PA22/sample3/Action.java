public abstract class Action {
    protected int initiator;

    public Action() {
        this.initiator = 0;
    }

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
