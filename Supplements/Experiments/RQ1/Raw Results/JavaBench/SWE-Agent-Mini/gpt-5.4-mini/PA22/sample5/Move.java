public abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public Move() {
    }

    public abstract Position nextPosition(Position currentPosition);
}
