/**
 * Move result when the player cannot move at all.
 */
public class Invalid extends MoveResult {

    public Invalid() {
    }

    public Invalid(final Position newPosition) {
        super(newPosition);
    }
}
