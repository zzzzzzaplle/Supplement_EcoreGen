public class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    public Down() {
        super(0);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}
