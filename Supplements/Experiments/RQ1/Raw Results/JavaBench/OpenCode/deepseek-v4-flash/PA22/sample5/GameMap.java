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

    public GameMap() {
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> parsedMap = new HashMap<>();
        Set<Position> dests = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;
        int playerCount = 0;
        int height = lines.length - 1;
        int width = 0;

        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            width = Math.max(width, line.length());
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y);
                if (c == '#') {
                    parsedMap.put(pos, new Wall());
                } else if (c == '.') {
                    parsedMap.put(pos, new Empty());
                } else if (c == '@') {
                    parsedMap.put(pos, new Empty());
                    dests.add(pos);
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    parsedMap.put(pos, new Player(playerId));
                    playerIds.add(playerId);
                    playerCount++;
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    parsedMap.put(pos, new Box(playerId));
                    boxCount++;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '#') continue;
                Position pos = Position.of(x, y);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    Entity e = parsedMap.get(pos);
                    if (e == null || !(e instanceof Wall)) {
                        if (e instanceof Player || e instanceof Box) {
                            throw new IllegalArgumentException("Player or box on boundary");
                        }
                        parsedMap.put(pos, new Wall());
                    }
                }
            }
        }

        if (playerCount == 0) {
            throw new IllegalArgumentException("No players found");
        }
        if (dests.size() != boxCount) {
            throw new IllegalArgumentException("Destinations count does not match boxes count");
        }
        for (Map.Entry<Position, Entity> entry : parsedMap.entrySet()) {
            if (entry.getValue() instanceof Box) {
                Box box = (Box) entry.getValue();
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references non-existent player");
                }
            }
        }

        return new GameMap(parsedMap, dests, undoLimit);
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

    public int getUndoLimitValue() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
