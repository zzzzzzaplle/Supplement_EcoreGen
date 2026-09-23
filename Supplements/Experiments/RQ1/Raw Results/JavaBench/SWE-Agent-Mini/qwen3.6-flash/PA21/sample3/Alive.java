import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Move result indicating a successful valid move where the player collected items.
 */
public class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    /**
     * Creates a default alive move result.
     */
    public Alive() {
        this(new Position(0, 0), new Position(0, 0));
    }

    /**
     * Creates an alive move result with the specified original and new positions.
     *
     * @param origPosition The original position before the move.
     * @param newPosition  The new position after the move.
     */
    public Alive(Position origPosition, Position newPosition) {
        super(origPosition, newPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    /**
     * Returns the list of positions where gems were collected during this move.
     *
     * @return The list of gem positions.
     */
    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    /**
     * Sets the collected gems list.
     *
     * @param collectedGems The list of gem positions.
     */
    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = Objects.requireNonNull(collectedGems);
    }

    /**
     * Returns the list of positions where extra lives were collected during this move.
     *
     * @return The list of extra life positions.
     */
    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    /**
     * Sets the collected extra lives list.
     *
     * @param collectedExtraLives The list of extra life positions.
     */
    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = Objects.requireNonNull(collectedExtraLives);
    }

    /**
     * Adds a gem position to the collected gems list.
     *
     * @param position The position of the collected gem.
     */
    public void addCollectedGem(Position position) {
        collectedGems.add(Objects.requireNonNull(position));
    }

    /**
     * Adds an extra life position to the collected extra lives list.
     *
     * @param position The position of the collected extra life.
     */
    public void addCollectedExtraLife(Position position) {
        collectedExtraLives.add(Objects.requireNonNull(position));
    }
}
