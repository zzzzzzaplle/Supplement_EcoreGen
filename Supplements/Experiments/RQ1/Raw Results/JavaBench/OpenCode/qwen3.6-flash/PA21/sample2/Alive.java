import java.util.List;
import java.util.ArrayList;

public class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

    public Alive() {
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(List<Position> collectedGems, List<Position> collectedExtraLives) {
        this.collectedGems = collectedGems != null ? collectedGems : new ArrayList<>();
        this.collectedExtraLives = collectedExtraLives != null ? collectedExtraLives : new ArrayList<>();
    }

    public Alive(Position origPosition, Position newPosition, List<Position> collectedGems, List<Position> collectedExtraLives) {
        super(origPosition, newPosition);
        this.collectedGems = collectedGems != null ? collectedGems : new ArrayList<>();
        this.collectedExtraLives = collectedExtraLives != null ? collectedExtraLives : new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }
}