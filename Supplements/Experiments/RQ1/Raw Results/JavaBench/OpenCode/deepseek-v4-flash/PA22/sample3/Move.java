public abstract class Move extends Action {
    public Move() {
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
