public class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Up() {
        super(0);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}
