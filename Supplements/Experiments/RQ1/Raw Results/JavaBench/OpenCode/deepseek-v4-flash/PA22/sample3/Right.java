public class Right extends Move {
    public Right() {
    }

    public Right(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}
