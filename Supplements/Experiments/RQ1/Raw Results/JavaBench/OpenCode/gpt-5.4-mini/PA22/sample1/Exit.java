public class Exit extends Action {
    public Exit() {
    }

    public Exit(int initiator) {
        super(initiator);
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}
