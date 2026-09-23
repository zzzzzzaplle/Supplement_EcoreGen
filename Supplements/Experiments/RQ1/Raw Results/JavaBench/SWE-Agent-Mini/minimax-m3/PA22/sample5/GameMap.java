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
        int startLine = 0;
        if (lines.length > 0 && lines[0].matches("-?\\d+")) {
            undoLimit = Integer.parseInt(lines[0].trim());
            startLine = 1;
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        int boxCount = 0;
        int destCount = 0;
        int maxX = 0;
        int maxY = 0;

        for (int y = startLine; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - startLine);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        map.put(pos, new Empty());
                        destinations.add(pos);
                        destCount++;
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int pid = c - 'A';
                            map.put(pos, new Player(pid));
                            playerIds.add(pid);
                        } else if (c >= 'a' && c <= 'z') {
                            int pid = c - 'a';
                            map.put(pos, new Box(pid));
                            boxPlayerIds.add(pid);
                            boxCount++;
                        }
                        break;
                }
                if (x > maxX) maxX = x;
                if (y - startLine > maxY) maxY = y - startLine;
            }
        }

        if (playerIds.isEmpty()) {
            throw new RuntimeException("Map must have at least one player.");
        }
        if (boxCount != destCount) {
            throw new RuntimeException("Number of boxes must equal number of destinations.");
        }
        for (int pid : boxPlayerIds) {
            if (!playerIds.contains(pid)) {
                throw new RuntimeException("Box references invalid player ID: " + pid);
            }
        }

        validateClosedBoundary(map, maxX, maxY);

        GameMap gm = new GameMap(map, destinations, undoLimit);
        return gm;
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int maxX, int maxY) {
        for (int x = 0; x <= maxX; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            if (!(map.get(top) instanceof Wall) || !(map.get(bottom) instanceof Wall)) {
                throw new RuntimeException("Map boundary must be closed (top/bottom).");
            }
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            if (!(map.get(left) instanceof Wall) || !(map.get(right) instanceof Wall)) {
                throw new RuntimeException("Map boundary must be closed (left/right).");
            }
        }
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
