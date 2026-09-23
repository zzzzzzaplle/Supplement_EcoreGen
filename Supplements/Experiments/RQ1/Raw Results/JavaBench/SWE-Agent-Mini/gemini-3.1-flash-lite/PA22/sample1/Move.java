public abstract class Move extends Action {
    protected Move() {}
    protected Move(int initiator) { super(initiator); }
    public abstract Position nextPosition(Position currentPosition);
}
