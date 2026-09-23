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
        this.map = new HashMap<>();
        this.destinations = Collections.emptySet();
        this.undoLimit = 0;
        this.maxWidth = 0;
        this.maxHeight = 0;
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

    public Map<Position, Entity> getMap() {
        return map;
    }

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }

    public int getMaxWidthField() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeightField() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public int getUndoLimitField() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\\r?\\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid map text");
        }
        int undoLimit = Integer.parseInt(lines[0].trim());

        Map<Position, Entity> parsedMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;

        int maxX = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            if (line.length() > maxX) {
                maxX = line.length();
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);
                if (c == '#') {
                    parsedMap.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                } else if (c == '.') {
                    // empty
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    parsedMap.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    parsedMap.put(pos, new Box(playerId));
                    boxCount++;
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must contain at least one player.");
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (int id : playerIds) {
            // boxes reference valid player IDs
        }

        int mapHeight = lines.length - 1;
        for (int x = 0; x < maxX; x++) {
            if (!(parsedMap.get(new Position(x, 0)) instanceof Wall)
                    || !(parsedMap.get(new Position(x, mapHeight - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary must be closed.");
            }
        }
        for (int y = 0; y < mapHeight; y++) {
            Entity left = parsedMap.get(new Position(0, y));
            Entity right = parsedMap.get(new Position(maxX - 1, y));
            if (!(left instanceof Wall) || !(right instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary must be closed.");
            }
        }

        return new GameMap(parsedMap, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (map instanceof HashMap) {
            map.put(position, entity);
        } else {
            Map<Position, Entity> mutable = new HashMap<>(map);
            mutable.put(position, entity);
            this.map = mutable;
        }
    }

    public Set<Position> getDestinationsSet() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
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

    public int getMaxHeight() {
        return maxHeight;
    }
}
