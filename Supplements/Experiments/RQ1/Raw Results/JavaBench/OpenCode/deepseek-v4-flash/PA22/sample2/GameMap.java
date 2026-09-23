import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map must have at least 2 lines (undo limit + map rows)");
        }

        int undoLimit = Integer.parseInt(lines[0].trim());
        String[] mapRows = new String[lines.length - 1];
        System.arraycopy(lines, 1, mapRows, 0, mapRows.length);

        int height = mapRows.length;
        int width = 0;
        for (String row : mapRows) {
            if (row.length() > width) {
                width = row.length();
            }
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        for (int y = 0; y < height; y++) {
            String row = mapRows[y];
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                Position pos = Position.of(x, y);
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

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references player ID " + boxPlayerId + " that does not exist in the map");
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    Entity entity = map.get(pos);
                    if (!(entity instanceof Wall)) {
                        throw new IllegalArgumentException("Map must have a closed boundary");
                    }
                }
            }
        }

        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            Position p = entry.getKey();
            if (p.x() >= width - 1 || p.x() <= 0 || p.y() >= height - 1 || p.y() <= 0) {
                if (!(entry.getValue() instanceof Wall)) {
                    throw new IllegalArgumentException("Map must have a closed boundary");
                }
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (entity instanceof Wall) {
            return;
        }
        Map<Position, Entity> mutableMap = new HashMap<>(map);
        if (entity instanceof Empty) {
            mutableMap.remove(position);
        } else {
            mutableMap.put(position, entity);
        }
        this.map = Collections.unmodifiableMap(mutableMap);
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
