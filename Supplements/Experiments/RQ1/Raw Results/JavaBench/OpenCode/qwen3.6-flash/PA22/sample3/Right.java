public class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Right() {
        super(0);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}
