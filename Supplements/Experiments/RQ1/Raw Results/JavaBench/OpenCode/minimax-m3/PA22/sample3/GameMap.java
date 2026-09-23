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
        this.undoLimit = -1;
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
            throw new IllegalArgumentException("Map text is null");
        }
        String[] lines = mapText.split("\\r?\\n");
        int undoLimit = -1;
        int startLine = 0;
        if (lines.length > 0) {
            try {
                undoLimit = Integer.parseInt(lines[0].trim());
                startLine = 1;
            } catch (NumberFormatException ignored) {
                undoLimit = -1;
                startLine = 0;
            }
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();

        int maxX = 0;
        int maxY = 0;
        for (int y = startLine; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y - startLine);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int pid = c - 'A';
                    map.put(pos, new Player(pid));
                    playerIds.add(pid);
                } else if (c >= 'a' && c <= 'z') {
                    int pid = c - 'a';
                    map.put(pos, new Box(pid));
                } else if (c == ' ') {
                    map.put(pos, new Empty());
                }
                if (x > maxX) maxX = x;
                if ((y - startLine) > maxY) maxY = y - startLine;
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must contain at least one player");
        }

        int boxCount = 0;
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                boxCount++;
                int pid = ((Box) e).getPlayerId();
                if (!playerIds.contains(pid)) {
                    throw new IllegalArgumentException("Box references invalid player id: " + pid);
                }
            }
        }

        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        validateClosedBoundary(map, maxX + 1, maxY + 1);

        return new GameMap(map, destinations, undoLimit);
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int w, int h) {
        for (int x = 0; x < w; x++) {
            Position top = Position.of(x, 0);
            Position bottom = Position.of(x, h - 1);
            if (!(map.get(top) instanceof Wall) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed");
            }
        }
        for (int y = 0; y < h; y++) {
            Position left = Position.of(0, y);
            Position right = Position.of(w - 1, y);
            if (!(map.get(left) instanceof Wall) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed");
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
        if (undoLimit < 0) {
            return Optional.empty();
        }
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
