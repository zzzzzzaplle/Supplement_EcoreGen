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
        if (mapText == null) {
            throw new IllegalArgumentException("Map text is null.");
        }
        String[] lines = mapText.split("\\r?\\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Map is empty.");
        }

        int undoLimit = 0;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be undo limit integer.");
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();

        int maxX = 0;
        int maxY = 0;

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y - 1);
                if (x > maxX) maxX = x;
                if (y - 1 > maxY) maxY = y - 1;
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    map.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int id = c - 'a';
                    map.put(pos, new Box(id));
                } else {
                    // ignore unknown characters
                }
            }
        }

        // Validate closed boundary: every cell on the outer rectangle must be a wall.
        int width = maxX + 1;
        int height = maxY + 1;
        for (int x = 0; x < width; x++) {
            Position top = Position.of(x, 0);
            Position bottom = Position.of(x, height - 1);
            Entity eTop = map.get(top);
            Entity eBottom = map.get(bottom);
            if (!(eTop instanceof Wall) || !(eBottom instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (top/bottom).");
            }
        }
        for (int y = 0; y < height; y++) {
            Position left = Position.of(0, y);
            Position right = Position.of(width - 1, y);
            Entity eLeft = map.get(left);
            Entity eRight = map.get(right);
            if (!(eLeft instanceof Wall) || !(eRight instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (left/right).");
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one player.");
        }

        // Count boxes and destinations
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (boxCount != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes must equal number of destinations.");
        }

        // Validate each box's playerId exists among players
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                int pid = ((Box) e).getPlayerId();
                if (!playerIds.contains(pid)) {
                    throw new IllegalArgumentException("Box references unknown player id: " + pid);
                }
            }
        }

        GameMap gm = new GameMap(map, destinations, undoLimit);
        return gm;
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

    public int getUndoLimitRaw() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
