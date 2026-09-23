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
            width = Math.max(width, lines[i].length());
        }
        height = Math.max(height, 0);
        width = Math.max(width, 0);

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();

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
                    map.put(pos, new Player(c - 'A'));
                    playerIds.add(c - 'A');
                } else if (c >= 'a' && c <= 'z') {
                    map.put(pos, new Box(c - 'a'));
                    playerIds.add(c - 'a');
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        int boxCount = 0;
        for (Entity entity : map.values()) {
            if (entity instanceof Box) {
                boxCount++;
                Box box = (Box) entity;
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references invalid player ID: " + box.getPlayerId());
                }
            }
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations (" + destinations.size() + ") must equal number of boxes (" + boxCount + ")");
        }

        boolean closedBoundary = checkClosedBoundary(map, width, height);
        if (!closedBoundary) {
            throw new IllegalArgumentException("Map must have a closed boundary");
        }

        return new GameMap(map, destinations, undoLimit);
    }

    private static boolean checkClosedBoundary(Map<Position, Entity> map, int width, int height) {
        for (int x = 0; x < width; x++) {
            if (!(map.get(Position.of(x, 0)) instanceof Wall)) return false;
            if (!(map.get(Position.of(x, height - 1)) instanceof Wall)) return false;
        }
        for (int y = 0; y < height; y++) {
            if (!(map.get(Position.of(0, y)) instanceof Wall)) return false;
            if (!(map.get(Position.of(width - 1, y)) instanceof Wall)) return false;
        }
        return true;
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
