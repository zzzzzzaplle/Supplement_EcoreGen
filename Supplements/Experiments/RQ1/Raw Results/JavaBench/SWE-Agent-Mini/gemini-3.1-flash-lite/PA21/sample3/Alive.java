import java.util.List;
import java.util.ArrayList;

public class Alive extends Valid {
    private List<Position> collectedGems = new ArrayList<>();
    private List<Position> collectedExtraLives = new ArrayList<>();

    public Alive() {}
    public List<Position> getCollectedGems() { return collectedGems; }
    public void setCollectedGems(List<Position> gems) { this.collectedGems = gems; }
    public List<Position> getCollectedExtraLives() { return collectedExtraLives; }
    public void setCollectedExtraLives(List<Position> lives) { this.collectedExtraLives = lives; }
}
