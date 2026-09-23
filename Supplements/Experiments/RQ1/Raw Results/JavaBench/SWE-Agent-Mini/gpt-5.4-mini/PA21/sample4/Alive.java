import java.util.ArrayList;
import java.util.List;

public class Alive extends Valid {
    private List<Position> collectedGems = new ArrayList<>();
    private List<Position> collectedExtraLives = new ArrayList<>();

    public Alive() {
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
