/**
 * Move action for moving left.
 */
public class Left extends Move {

    public Left() {
        super(0);
    }

    public Left(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}
