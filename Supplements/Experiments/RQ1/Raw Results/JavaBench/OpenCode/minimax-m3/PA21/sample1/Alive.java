import java.util.List;

public class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = null;
        this.collectedExtraLives = null;
    }

    public Alive(Position newPosition, Position origPosition) {
        super(newPosition, origPosition);
        this.collectedGems = null;
        this.collectedExtraLives = null;
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}
