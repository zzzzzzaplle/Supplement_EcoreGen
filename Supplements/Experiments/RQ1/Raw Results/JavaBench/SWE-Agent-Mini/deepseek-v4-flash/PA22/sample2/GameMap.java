import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {
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
        String[] lines = mapText.split("\n");
        // First line is undo limit
        int undoLimit = Integer.parseInt(lines[0].trim());
        
        int height = lines.length - 1;
        
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c == '@') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    map.put(pos, new Player(playerId));
                    playerIds.add(playerId);
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    map.put(pos, new Box(playerId));
                    boxPlayerIds.add(playerId);
                }
                // spaces are ignored (no entity)
            }
        }
        
        // Validate closed boundary: all positions on the outer edge must be walls
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c != '#' && c != ' ') {
                    if (y == 0 || y == height - 1 || x == 0 || x == line.length() - 1) {
                        throw new IllegalArgumentException("Map must have closed boundary: non-wall at edge (" + x + "," + y + ")");
                    }
                }
            }
        }
        
        // Validate at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        
        // Validate destinations count equals boxes count
        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        
        // Validate boxes reference valid player IDs
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references non-existent player ID: " + boxPlayerId);
            }
        }
        
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        Map<Position, Entity> mutableMap = new HashMap<>(map);
        mutableMap.put(position, entity);
        this.map = Collections.unmodifiableMap(mutableMap);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
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

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
