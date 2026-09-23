import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A valid move result where the player is still alive and collected items.
 */
public class Alive extends Valid {

    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition, Position origPosition) {
        super(newPosition, origPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    /**
     * Gets the list of positions of collected gems.
     *
     * @return the list of collected gem positions.
     */
    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    /**
     * Sets the list of positions of collected gems.
     *
     * @param collectedGems the list of collected gem positions.
     */
    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = Objects.requireNonNull(collectedGems);
    }

    /**
     * Gets the list of positions of collected extra lives.
     *
     * @return the list of collected extra life positions.
     */
    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    /**
     * Sets the list of positions of collected extra lives.
     *
     * @param collectedExtraLives the list of collected extra life positions.
     */
    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = Objects.requireNonNull(collectedExtraLives);
    }
}
