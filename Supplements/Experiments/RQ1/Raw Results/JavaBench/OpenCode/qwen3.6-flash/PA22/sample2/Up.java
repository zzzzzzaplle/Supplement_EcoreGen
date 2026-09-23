/**
 * Move action for moving up.
 */
public class Up extends Move {

    public Up() {
        super(0);
    }

    public Up(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}
