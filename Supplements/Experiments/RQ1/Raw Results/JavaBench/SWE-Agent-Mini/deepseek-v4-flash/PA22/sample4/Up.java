public class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Up() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}
