/**
 * Move action for moving down.
 */
public class Down extends Move {

    public Down() {
        super(0);
    }

    public Down(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}
