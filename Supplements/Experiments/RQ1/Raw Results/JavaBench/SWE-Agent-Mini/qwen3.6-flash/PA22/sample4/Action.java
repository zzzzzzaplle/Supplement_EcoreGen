abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public Action() {
        this.initiator = -1;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
