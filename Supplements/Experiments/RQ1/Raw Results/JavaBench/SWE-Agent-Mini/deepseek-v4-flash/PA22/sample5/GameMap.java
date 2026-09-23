import java.util.HashMap;
import java.util.HashSet;
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
    }

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(map);
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        
        int height = lines.length - 1;
        int width = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > width) {
                width = lines[i].length();
            }
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        // First pass: collect entities
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y);
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
            }
        }

        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }

        // Validate: number of destinations equals number of boxes
        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }

        // Validate: boxes reference valid player IDs
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references player ID " + boxPlayerId + " which does not exist.");
            }
        }

        // Validate: closed boundary (all border cells must be walls)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    Entity entity = map.get(pos);
                    if (entity == null || !(entity instanceof Wall)) {
                        throw new IllegalArgumentException("Map boundary is not closed at " + pos);
                    }
                }
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (entity == null) {
            map.remove(position);
        } else {
            map.put(position, entity);
        }
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
