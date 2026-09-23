abstract class Move extends Action {

    protected Move(int initiator) {
        super(initiator);
    }

    protected Move() {
        this(0);
    }

    public abstract Position nextPosition(Position currentPosition);
}
