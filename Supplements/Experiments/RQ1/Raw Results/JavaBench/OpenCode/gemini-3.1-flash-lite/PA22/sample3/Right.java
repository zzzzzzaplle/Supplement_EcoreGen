public class Right extends Move {
    public Right() {}

    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) { return null; }
}
