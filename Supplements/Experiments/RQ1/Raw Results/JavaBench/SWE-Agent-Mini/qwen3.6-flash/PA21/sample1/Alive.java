import java.util.ArrayList;
import java.util.List;

/**
 * Represents a valid move result where the player did not die and collected gems/extra lives.
 */
public class Alive extends Valid {
    public List<Position> collectedGems = new ArrayList<>();
    public List<Position> collectedExtraLives = new ArrayList<>();

    /**
     * Creates a new Alive move result.
     *
     * @param origPosition The original position of the player.
     * @param newPosition  The new position of the player.
     */
    public Alive(Position origPosition, Position newPosition) {
        super(origPosition, newPosition);
    }

    public Alive() {
    }
    /**
     * Gets the list of collected gems.
     *
     * @return The list of collected gems.
     */
    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    /**
     * Sets the list of collected gems.
     *
     * @param collectedGems The list of collected gems.
     */
    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    /**
     * Gets the list of collected extra lives.
     *
     * @return The list of collected extra lives.
     */
    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    /**
     * Sets the list of collected extra lives.
     *
     * @param collectedExtraLives The list of collected extra lives.
     */
    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}
