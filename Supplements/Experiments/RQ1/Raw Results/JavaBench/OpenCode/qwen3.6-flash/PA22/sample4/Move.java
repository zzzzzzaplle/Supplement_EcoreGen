public abstract class Move extends Action {
    public Move() {
        super(-1);
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
