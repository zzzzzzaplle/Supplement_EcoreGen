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

    public GameMap() {
        this.maxWidth = 0;
        this.maxHeight = 0;
        this.destinations = Collections.unmodifiableSet(new HashSet<>());
        this.undoLimit = 0;
        this.map = new HashMap<>();
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
        String[] lines = mapText.split("\\r?\\n");
        int undoLimit = 0;
        int startIndex = 0;
        if (lines.length > 0) {
            try {
                undoLimit = Integer.parseInt(lines[0].trim());
                startIndex = 1;
            } catch (NumberFormatException ignored) {
                startIndex = 0;
            }
        }
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int maxX = 0;
        int maxY = 0;
        for (int y = startIndex; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - startIndex);
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
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int id = c - 'A';
                            map.put(pos, new Player(id));
                            playerIds.add(id);
                        } else if (c >= 'a' && c <= 'z') {
                            int id = c - 'a';
                            map.put(pos, new Box(id));
                        }
                        break;
                }
                if (x > maxX) maxX = x;
            }
            if ((y - startIndex) > maxY) maxY = y - startIndex;
        }
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                Box b = (Box) e;
                if (!playerIds.contains(b.getPlayerId())) {
                    throw new IllegalArgumentException("Box references invalid player id: " + b.getPlayerId());
                }
            }
        }
        validateClosedBoundary(map, maxX, maxY);
        return new GameMap(map, destinations, undoLimit);
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int maxX, int maxY) {
        for (int x = 0; x <= maxX; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            if (!(map.get(top) instanceof Wall) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed.");
            }
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            if (!(map.get(left) instanceof Wall) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed.");
            }
        }
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        if (undoLimit < 0) return Optional.empty();
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
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

    public int getUndoLimitRaw() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
