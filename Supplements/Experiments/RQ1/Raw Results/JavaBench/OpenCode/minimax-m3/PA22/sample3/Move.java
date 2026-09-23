public abstract class Move extends Action {

    public Move() {
        super();
    }

    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
