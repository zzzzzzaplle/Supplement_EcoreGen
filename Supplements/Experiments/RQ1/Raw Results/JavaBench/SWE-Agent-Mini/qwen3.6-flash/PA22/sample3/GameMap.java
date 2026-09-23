import java.util.*;

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

    public Map<Position, Entity> getEntities() {
        return new HashMap<>(map);
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map must have at least an undo limit line and game lines");
        }
        
        int undoLimit = Integer.parseInt(lines[0].trim());
        
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                if (x >= line.length()) break;
                char c = line.charAt(x);
                if (c == '#') {
                    map.put(Position.of(x, y), new Wall());
                } else if (c == '@') {
                    destinations.add(Position.of(x, y));
                    map.put(Position.of(x, y), new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    map.put(Position.of(x, y), new Player(playerId));
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    map.put(Position.of(x, y), new Box(playerId));
                } else if (c == ' ') {
                    map.put(Position.of(x, y), new Empty());
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
}
