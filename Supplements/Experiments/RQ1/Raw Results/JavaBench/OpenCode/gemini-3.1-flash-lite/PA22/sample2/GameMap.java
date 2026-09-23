import java.util.*;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {}
    
    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(map);
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }
    public static GameMap parse(String mapText) { return null; }
    public Entity getEntity(Position position) { return null; }
    public void putEntity(Position position, Entity entity) {}
    public Set<Position> getDestinations() { return destinations; }
    public Optional<Integer> getUndoLimit() { return null; }
    public Set<Integer> getPlayerIds() { return null; }
    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }
}
