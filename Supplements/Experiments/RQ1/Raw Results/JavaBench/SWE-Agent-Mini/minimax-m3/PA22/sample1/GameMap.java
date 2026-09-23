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
        this.destinations = Collections.unmodifiableSet(new HashSet<>());
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

    public static GameMap parse(String mapText) {
        if (mapText == null) {
            throw new IllegalArgumentException("Map text is null");
        }
        String[] lines = mapText.split("\\r?\\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
        }

        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;
        int destCount = 0;
        int maxX = 0;
        int maxY = 0;

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);
                if (x > maxX) maxX = x;
                if ((y - 1) > maxY) maxY = y - 1;
                switch (c) {
                    case '#':
                        entityMap.put(pos, new Wall());
                        break;
                    case '@':
                        entityMap.put(pos, new Empty());
                        destinations.add(pos);
                        destCount++;
                        break;
                    case '.':
                        entityMap.put(pos, new Empty());
                        break;
                    case ' ':
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int playerId = c - 'A';
                            entityMap.put(pos, new Player(playerId));
                            playerIds.add(playerId);
                        } else if (c >= 'a' && c <= 'z') {
                            int playerId = c - 'a';
                            entityMap.put(pos, new Box(playerId));
                            playerIds.add(playerId);
                            boxCount++;
                        } else {
                            throw new IllegalArgumentException("Invalid map character: " + c);
                        }
                        break;
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        if (destCount != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Check that all box player IDs exist in playerIds
        for (Map.Entry<Position, Entity> e : entityMap.entrySet()) {
            if (e.getValue() instanceof Box) {
                Box b = (Box) e.getValue();
                if (!playerIds.contains(b.getPlayerId())) {
                    throw new IllegalArgumentException("Box references non-existent player id: " + b.getPlayerId());
                }
            }
        }

        // Validate closed boundary
        for (int x = 0; x <= maxX; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            Entity topE = entityMap.get(top);
            Entity bottomE = entityMap.get(bottom);
            if (!(topE instanceof Wall) || !(bottomE instanceof Wall)) {
                throw new IllegalArgumentException("Map must have a closed boundary");
            }
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            Entity leftE = entityMap.get(left);
            Entity rightE = entityMap.get(right);
            if (!(leftE instanceof Wall) || !(rightE instanceof Wall)) {
                throw new IllegalArgumentException("Map must have a closed boundary");
            }
        }

        return new GameMap(entityMap, destinations, undoLimit);
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

    public Optional<Integer> getUndoLimit() {
        if (this.undoLimit == -1) {
            return Optional.empty();
        }
        return Optional.of(this.undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : this.map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            }
        }
        return ids;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return this.map;
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

    public int getUndoLimitValue() {
        return this.undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
