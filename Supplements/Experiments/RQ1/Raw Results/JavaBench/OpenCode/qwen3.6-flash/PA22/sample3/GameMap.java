import java.util.*;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(map);
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.trim().split("\n");
        String firstLine = lines[0].trim();
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(firstLine);
        } catch (NumberFormatException e) {
            undoLimit = -1;
        }

        List<String> gridLines = Arrays.asList(lines).subList(1, lines.length);
        List<String> filteredLines = new ArrayList<>();
        for (String line : gridLines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                filteredLines.add(trimmed);
            }
        }

        if (filteredLines.isEmpty()) {
            throw new IllegalArgumentException("Map is empty");
        }

        int height = filteredLines.size();
        int width = filteredLines.get(0).length();
        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destSet = new HashSet<>();

        // Track players and boxes
        Set<Integer> playerIds = new HashSet<>();
        List<Integer> boxPlayerIds = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            String line = filteredLines.get(y);
            if (line.length() > width) {
                width = line.length();
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);

                if (c == '#') {
                    entityMap.put(pos, new Wall());
                } else if (c == '@') {
                    destSet.add(pos);
                    entityMap.put(pos, new Empty());
                } else if (c == '.') {
                    entityMap.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    playerIds.add(c - 'A');
                    Player player = new Player(c - 'A');
                    entityMap.put(pos, player);
                } else if (c >= 'a' && c <= 'z') {
                    int boxPlayerId = c - 'a';
                    boxPlayerIds.add(boxPlayerId);
                    Box box = new Box(boxPlayerId);
                    entityMap.put(pos, box);
                }
            }
        }

        // Validate at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one player in the map");
        }

        // Validate destinations == boxes
        if (destSet.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate box player IDs exist
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references a player ID that does not exist: " + boxPlayerId);
            }
        }

        // Validate closed boundary
        validateClosedBoundary(entityMap, width, height);

        // Fill in missing edges of the bounding box as walls
        for (int x = 0; x < width; x++) {
            entityMap.computeIfAbsent(new Position(x, 0), pos -> new Wall());
            entityMap.computeIfAbsent(new Position(x, height - 1), pos -> new Wall());
        }
        for (int y = 0; y < height; y++) {
            entityMap.computeIfAbsent(new Position(0, y), pos -> new Wall());
            entityMap.computeIfAbsent(new Position(width - 1, y), pos -> new Wall());
        }

        // Update width/height based on filled boundary
        width = 0;
        height = 0;
        for (Position pos : entityMap.keySet()) {
            if (pos.x() + 1 > width) width = pos.x() + 1;
            if (pos.y() + 1 > height) height = pos.y() + 1;
        }

        GameMap gameMap = new GameMap(entityMap, destSet, undoLimit);
        gameMap.maxWidth = width;
        gameMap.maxHeight = height;
        return gameMap;
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int width, int height) {
        // Check top and bottom rows
        for (int x = 0; x < width; x++) {
            if (!map.containsKey(new Position(x, 0)) || !(map.get(new Position(x, 0)) instanceof Wall)) {
                throw new IllegalArgumentException("Top boundary is not closed");
            }
            if (!map.containsKey(new Position(x, height - 1)) || !(map.get(new Position(x, height - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Bottom boundary is not closed");
            }
        }
        // Check left and right columns
        for (int y = 0; y < height; y++) {
            if (!map.containsKey(new Position(0, y)) || !(map.get(new Position(0, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Left boundary is not closed");
            }
            if (!map.containsKey(new Position(width - 1, y)) || !(map.get(new Position(width - 1, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Right boundary is not closed");
            }
        }
    }

    public Entity getEntity(Position position) {
        return this.map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return new HashSet<>(this.destinations);
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(this.undoLimit);
    }

    public int getUndoLimitValue() {
        return this.undoLimit;
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
}
