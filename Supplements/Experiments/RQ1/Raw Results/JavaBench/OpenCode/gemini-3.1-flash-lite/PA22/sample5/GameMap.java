import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {
        this.map = new HashMap<>();
        this.destinations = Collections.emptySet();
    }

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

    public static GameMap parse(String mapText) {
        return new GameMap();
    }

    public Map<Position, Entity> getMap() { return map; }
    public void setMap(Map<Position, Entity> map) { this.map = map; }

    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }

    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }

    public Set<Position> getDestinations() { return destinations; }
    public void setDestinations(Set<Position> destinations) { this.destinations = destinations; }

    public int getUndoLimit() { return undoLimit; }
    public void setUndoLimit(int undoLimit) { this.undoLimit = undoLimit; }

    public Entity getEntity(Position position) { return map.get(position); }
    public void putEntity(Position position, Entity entity) { map.put(position, entity); }
    public Set<Integer> getPlayerIds() { return Collections.emptySet(); }
}
