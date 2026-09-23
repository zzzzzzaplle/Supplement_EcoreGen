public abstract class Move extends Action {
    public Move() {
        super();
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
