public class Down extends Move {
    public Down() {
    }

    public Down(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}
