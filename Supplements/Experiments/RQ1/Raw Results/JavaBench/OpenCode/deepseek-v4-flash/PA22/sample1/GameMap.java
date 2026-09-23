import java.util.Collections;
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
        String[] lines = mapText.split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;

        for (int y = 0; y < lines.length - 1; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    map.put(pos, new Empty());
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    map.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int id = c - 'a';
                    map.put(pos, new Box(id));
                    boxCount++;
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one player.");
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            if (entry.getValue() instanceof Box) {
                int playerId = ((Box) entry.getValue()).getPlayerId();
                if (!playerIds.contains(playerId)) {
                    throw new IllegalArgumentException("Box references non-existent player ID: " + playerId);
                }
            }
        }
        int maxX = map.keySet().stream().mapToInt(Position::x).max().orElse(0);
        int maxY = map.keySet().stream().mapToInt(Position::y).max().orElse(0);
        for (int x = 0; x <= maxX; x++) {
            Position top = Position.of(x, 0);
            Position bottom = Position.of(x, maxY);
            if (!map.containsKey(top) || !(map.get(top) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary.");
            }
            if (!map.containsKey(bottom) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary.");
            }
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = Position.of(0, y);
            Position right = Position.of(maxX, y);
            if (!map.containsKey(left) || !(map.get(left) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary.");
            }
            if (!map.containsKey(right) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary.");
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        if (undoLimit == -1) {
            return Optional.empty();
        }
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity entity : map.values()) {
            if (entity instanceof Player) {
                ids.add(((Player) entity).getId());
            }
        }
        return ids;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return map;
    }

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
