import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class Alive extends Valid {

    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition) {
        super(newPosition);
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = Objects.requireNonNull(collectedGems);
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = Objects.requireNonNull(collectedExtraLives);
    }
}
