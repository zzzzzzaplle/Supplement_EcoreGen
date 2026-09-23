import java.util.Objects;

/**
 * An invalid move result - the player could not move at all.
 */
public class Invalid extends MoveResult {

    public Invalid() {
        super();
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}
