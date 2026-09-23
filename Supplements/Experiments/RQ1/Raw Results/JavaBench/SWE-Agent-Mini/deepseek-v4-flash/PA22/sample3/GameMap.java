import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        int boxCount = 0;
        
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = new Position(x, y);
                
                if (ch == '#') {
                    map.put(pos, new Wall());
                } else if (ch == '@') {
                    destinations.add(pos);
                    map.put(pos, new Empty());
                } else if (ch == '.') {
                    map.put(pos, new Empty());
                } else if (ch >= 'A' && ch <= 'Z') {
                    int playerId = ch - 'A';
                    playerIds.add(playerId);
                    map.put(pos, new Player(playerId));
                } else if (ch >= 'a' && ch <= 'z') {
                    int playerId = ch - 'a';
                    playerIds.add(playerId);
                    map.put(pos, new Box(playerId));
                    boxCount++;
                }
            }
        }
        
        // Validate: closed boundary
        for (int x = 0; x < width; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, height - 1);
            if (!(map.get(top) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (top)");
            }
            if (!(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (bottom)");
            }
        }
        for (int y = 0; y < height; y++) {
            Position left = new Position(0, y);
            Position right = new Position(width - 1, y);
            if (!(map.get(left) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (left)");
            }
            if (!(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (right)");
            }
        }
        
        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        
        // Validate: number of destinations equals number of boxes
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations (" + destinations.size() + 
                ") must equal number of boxes (" + boxCount + ")");
        }
        
        // Validate: boxes reference valid player IDs
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            if (entry.getValue() instanceof Box) {
                Box box = (Box) entry.getValue();
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references player " + box.getPlayerId() + 
                        " which does not exist in the map");
                }
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
        if (undoLimit == 0) {
            return Optional.empty();
        }
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
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
