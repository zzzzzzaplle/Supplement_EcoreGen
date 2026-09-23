public class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Right() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}
