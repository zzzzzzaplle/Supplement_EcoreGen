import java.util.List;

public class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
    }

    public Alive(Position newPosition, Position origPosition, List<Position> collectedGems, List<Position> collectedExtraLives) {
        super(newPosition, origPosition);
        this.collectedGems = collectedGems;
        this.collectedExtraLives = collectedExtraLives;
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
