import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Alive extends Valid {

    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive(Position origPosition, Position newPosition) {
        super(origPosition, newPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
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

    public void addCollectedGem(Position pos) {
        collectedGems.add(pos);
    }

    public void addCollectedExtraLife(Position pos) {
        collectedExtraLives.add(pos);
    }
}
