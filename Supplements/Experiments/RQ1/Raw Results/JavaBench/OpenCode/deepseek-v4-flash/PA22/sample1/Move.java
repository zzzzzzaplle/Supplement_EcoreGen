public abstract class Move extends Action {
    public Move(int initiator) {
        super(initiator);
    }

    public Move() {
    }

    public abstract Position nextPosition(Position currentPosition);
}
