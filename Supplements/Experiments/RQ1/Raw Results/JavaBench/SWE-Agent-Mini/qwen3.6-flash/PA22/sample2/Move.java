
/**
 * Abstract base class for movement actions.
 */
public abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}
