public class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Up() {
    }

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}
