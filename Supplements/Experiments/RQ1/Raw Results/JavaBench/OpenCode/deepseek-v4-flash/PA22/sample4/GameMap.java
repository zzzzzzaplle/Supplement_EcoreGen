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
            if (lines[i].length() > width) {
                width = lines[i].length();
            }
        }
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = Position.of(x, y);
                if (ch == '#') {
                    map.put(pos, new Wall());
                } else if (ch == '.') {
                    map.put(pos, new Empty());
                } else if (ch == '@') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (ch >= 'A' && ch <= 'Z') {
                    int playerId = ch - 'A';
                    map.put(pos, new Player(playerId));
                } else if (ch >= 'a' && ch <= 'z') {
                    int playerId = ch - 'a';
                    map.put(pos, new Box(playerId));
                }
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position pos = Position.of(x, y);
                if (!map.containsKey(pos)) {
                    map.put(pos, new Empty());
                }
            }
        }
        int playerCount = 0;
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;
        Set<Integer> boxPlayerIds = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                playerCount++;
                playerIds.add(((Player) entity).getId());
            } else if (entity instanceof Box) {
                boxCount++;
                boxPlayerIds.add(((Box) entity).getPlayerId());
            }
        }
        for (int boxPid : boxPlayerIds) {
            if (!playerIds.contains(boxPid)) {
                throw new IllegalArgumentException("Box references non-existent player: " + boxPid);
            }
        }
        if (playerCount == 0) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        validateClosedBoundary(map, width, height);
        return new GameMap(map, destinations, undoLimit);
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int width, int height) {
        for (int x = 0; x < width; x++) {
            Position top = Position.of(x, 0);
            Position bottom = Position.of(x, height - 1);
            if (!(map.get(top) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed at top/bottom");
            }
            if (!(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed at top/bottom");
            }
        }
        for (int y = 0; y < height; y++) {
            Position left = Position.of(0, y);
            Position right = Position.of(width - 1, y);
            if (!(map.get(left) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed at left/right");
            }
            if (!(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed at left/right");
            }
        }
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        Map<Position, Entity> mutableMap = new HashMap<>(map);
        mutableMap.put(position, entity);
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
        return map.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
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

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
