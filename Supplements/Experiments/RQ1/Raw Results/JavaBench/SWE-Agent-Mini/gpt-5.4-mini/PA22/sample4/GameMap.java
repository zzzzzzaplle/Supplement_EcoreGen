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

    public GameMap() {
    }

    public static GameMap parse(String mapText) {
        return null;
    }

    public Entity getEntity(Position position) {
        return null;
    }

    public void putEntity(Position position, Entity entity) {
    }

    public Set<Position> getDestinations() {
        return null;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.empty();
    }

    public Set<Integer> getPlayerIds() {
        return null;
    }

    public int getMaxWidth() {
        return 0;
    }

    public int getMaxHeight() {
        return 0;
    }
}
