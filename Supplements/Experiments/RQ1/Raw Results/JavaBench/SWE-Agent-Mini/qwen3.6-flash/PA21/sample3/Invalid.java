/**
 * Move result indicating the move was invalid.
 */
public class Invalid extends MoveResult {

    public Invalid() {
        super(null);
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}
