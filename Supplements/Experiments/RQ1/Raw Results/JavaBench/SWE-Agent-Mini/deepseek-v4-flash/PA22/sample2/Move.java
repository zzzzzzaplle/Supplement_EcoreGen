public abstract class Move extends Action {

    protected Move() {
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
