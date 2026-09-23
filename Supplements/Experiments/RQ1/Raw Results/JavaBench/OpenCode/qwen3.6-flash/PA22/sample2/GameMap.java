import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the game map in Sokoban.
 */
public class GameMap {

    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    private GameMap() {
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
        String[] lines = mapText.trim().split("\n");
        // First line is the undo limit
        int undoLimit = Integer.parseInt(lines[0].trim());

        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destPositions = new HashSet<>();

        // Parse the map
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);

                if (c == '#') {
                    entityMap.put(pos, new Wall());
                } else if (c == '@') {
                    destPositions.add(pos);
                    entityMap.put(pos, new Empty());
                } else if (c == '.') {
                    entityMap.put(pos, new Empty());
                } else if (Character.isUpperCase(c)) {
                    // Player
                    Player player = new Player(c - 'A');
                    entityMap.put(pos, player);
                } else if (Character.isLowerCase(c)) {
                    // Box
                    Box box = new Box(c - 'a');
                    entityMap.put(pos, box);
                }
            }
        }

        return new GameMap(entityMap, destPositions, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        ((HashMap<Position, Entity>) map).put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            if (entry.getValue() instanceof Player) {
                ids.add(((Player) entry.getValue()).getId());
            }
        }
        return ids;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return map;
    }
}
