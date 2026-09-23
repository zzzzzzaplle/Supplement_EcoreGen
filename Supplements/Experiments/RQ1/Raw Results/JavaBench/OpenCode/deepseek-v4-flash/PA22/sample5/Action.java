public abstract class Action {
    private int initiator;

    public Action(int initiator) {
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
