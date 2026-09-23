/**
 * Represents an invalid move result where the player could not move at all.
 */
public class Invalid extends MoveResult {
    public Invalid() {
    }
    /**
     * Creates a new Invalid move result with the same position.
     *
     * @param position The position where the player is (unchanged).
     */
    public Invalid(Position position) {
        super(position);
    }
}
