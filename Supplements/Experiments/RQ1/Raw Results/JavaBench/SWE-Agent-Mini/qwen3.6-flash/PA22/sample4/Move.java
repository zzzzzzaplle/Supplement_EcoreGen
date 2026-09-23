abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    protected Move() {
        super();
    }

    public abstract Position nextPosition(Position currentPosition);
}
