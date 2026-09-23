import java.util.*;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {}

    public Map<Position, Entity> getMap() { return map; }
    public void setMap(Map<Position, Entity> map) { this.map = map; }

    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }

    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }

    public Set<Position> getDestinations() { return destinations; }
    public void setDestinations(Set<Position> destinations) { this.destinations = destinations; }

    public int getUndoLimitValue() { return undoLimit; }
    public void setUndoLimit(int undoLimit) { this.undoLimit = undoLimit; }

    public static GameMap parse(String mapText) { return null; }
    public Entity getEntity(Position position) { return null; }
    public void putEntity(Position position, Entity entity) {}
    public Optional<Integer> getUndoLimit() { return null; }
    public Set<Integer> getPlayerIds() { return null; }
}
