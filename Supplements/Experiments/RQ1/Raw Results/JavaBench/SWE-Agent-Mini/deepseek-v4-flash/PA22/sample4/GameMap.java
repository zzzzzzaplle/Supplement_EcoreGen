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
        this.map = new HashMap<>();
        this.destinations = new HashSet<>();
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
        
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        map.put(pos, new Empty());
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    case ' ':
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int playerId = c - 'A';
                            playerIds.add(playerId);
                            map.put(pos, new Player(playerId));
                        } else if (c >= 'a' && c <= 'z') {
                            int playerId = c - 'a';
                            boxPlayerIds.add(playerId);
                            map.put(pos, new Box(playerId));
                        }
                        break;
                }
            }
        }
        
        // Validate: closed boundary - check that the outer boundary has walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    Entity entity = map.get(pos);
                    if (entity == null || !(entity instanceof Wall)) {
                        // Allow space outside the map
                        if (entity != null && !(entity instanceof Wall)) {
                            throw new IllegalArgumentException("Map must have closed boundary");
                        }
                    }
                }
            }
        }
        
        // Validate at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        
        // Validate number of destinations equals number of boxes
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        
        // Validate boxes reference valid player IDs
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references player ID " + boxPlayerId + " which does not exist");
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

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
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
