public abstract class Move extends Action {
    public Move(int initiator) {
        super(initiator);
    }

    public Move() {
        super();
    }

    public abstract Position nextPosition(Position currentPosition);
}
