public abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public Move() {
        super(0);
    }

    public abstract Position nextPosition(Position currentPosition);
}
