public class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    public Down() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}
