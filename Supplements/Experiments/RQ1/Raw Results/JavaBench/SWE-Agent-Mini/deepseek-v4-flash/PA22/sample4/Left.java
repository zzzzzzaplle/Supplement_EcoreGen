public class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    public Left() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}
