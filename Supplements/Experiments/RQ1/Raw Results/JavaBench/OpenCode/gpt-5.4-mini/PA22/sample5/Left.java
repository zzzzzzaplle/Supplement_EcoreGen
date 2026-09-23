public class Left extends Move {
    public Left() {
    }

    public Left(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return currentPosition;
    }
}
