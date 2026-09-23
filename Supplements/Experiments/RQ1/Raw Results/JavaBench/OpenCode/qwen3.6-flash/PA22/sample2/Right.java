/**
 * Move action for moving right.
 */
public class Right extends Move {

    public Right() {
        super(0);
    }

    public Right(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}
