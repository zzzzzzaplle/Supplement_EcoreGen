/**
 * Abstract base class for movement actions.
 */
public abstract class Move extends Action {

    public Move() {
        super(0);
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
