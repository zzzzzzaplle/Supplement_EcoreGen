import java.util.ArrayList;
import java.util.List;

public class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

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

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void addCollectedGem(final Position position) {
        collectedGems.add(position);
    }

    public void addCollectedExtraLife(final Position position) {
        collectedExtraLives.add(position);
    }
}
