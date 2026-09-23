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
            throw new IllegalArgumentException("Map text cannot be null");
        }
        final String[] lines = mapText.split("\\r?\\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }
        int undoLimit = -1;
        int startLine = 0;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
            startLine = 1;
        } catch (NumberFormatException ignored) {
            // first line is the map content
        }

        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;

        int maxX = 0;
        int maxY = 0;

        for (int y = startLine; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - startLine);
                switch (c) {
                    case '#':
                        entityMap.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        break;
                    case '.':
                        entityMap.put(pos, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int id = c - 'A';
                            entityMap.put(pos, new Player(id));
                            playerIds.add(id);
                        } else if (c >= 'a' && c <= 'z') {
                            int id = c - 'a';
                            entityMap.put(pos, new Box(id));
                            playerIds.add(id);
                            boxCount++;
                        }
                        break;
                }
                if (x > maxX) maxX = x;
                if ((y - startLine) > maxY) maxY = y - startLine;
            }
        }

        // Validate closed boundary
        for (int x = 0; x <= maxX; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            if (!(entityMap.get(top) instanceof Wall) || !(entityMap.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed");
            }
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            if (!(entityMap.get(left) instanceof Wall) || !(entityMap.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed");
            }
        }

        if (playerIds.stream().filter(id -> entityMap.values().stream()
                .anyMatch(e -> e instanceof Player && ((Player) e).getId() == id)).count() == 0) {
            throw new IllegalArgumentException("At least one player required");
        }

        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate that all box player ids correspond to existing players
        Set<Integer> actualPlayerIds = entityMap.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
        for (Entity e : entityMap.values()) {
            if (e instanceof Box) {
                int pid = ((Box) e).getPlayerId();
                if (!actualPlayerIds.contains(pid)) {
                    throw new IllegalArgumentException("Box references invalid player id: " + pid);
                }
            }
        }

        GameMap gm = new GameMap(entityMap, destinations, undoLimit);
        gm.maxWidth = maxX + 1;
        gm.maxHeight = maxY + 1;
        return gm;
    }

    public Entity getEntity(Position position) {
        return this.map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return this.destinations;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public Optional<Integer> getUndoLimit() {
        if (this.undoLimit < 0) {
            return Optional.empty();
        }
        return Optional.of(this.undoLimit);
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public Set<Integer> getPlayerIds() {
        return this.map.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return this.map;
    }

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }
}
