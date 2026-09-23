import java.util.List;
import java.util.ArrayList;

/**
 * Valid move result where the player survives (no mine hit).
 */
public class Alive extends Valid {

    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(final Position origPosition, final Position newPosition) {
        super(origPosition, newPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(final List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(final List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}
