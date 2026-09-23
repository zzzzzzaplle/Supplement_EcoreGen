
import java.util.*;

/**
 * Represents the game map/board.
 */
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

    public static GameMap parse(String mapText) {
        String[] lines = mapText.trim().split(System.lineSeparator());
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }

        // First line is the undo limit
        int undoLimit = Integer.parseInt(lines[0].trim());

        List<String> boardLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty()) {
                boardLines.add(line);
            }
        }

        if (boardLines.isEmpty()) {
            throw new IllegalArgumentException("No board content in map");
        }

        int height = boardLines.size();
        int width = boardLines.get(0).length();

        // Validate all lines have same length
        for (String line : boardLines) {
            if (line.length() != width) {
                throw new IllegalArgumentException("Map lines have inconsistent widths");
            }
        }

        Map<Position, Entity> gameMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Character, Integer> playerCount = new HashMap<>();
        List<Character> boxCharacters = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            String line = boardLines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);

                if (c == '#') {
                    gameMap.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    gameMap.put(pos, new Empty());
                } else if (c == '.') {
                    gameMap.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    playerCount.put(c, playerCount.getOrDefault(c, 0) + 1);
                    gameMap.put(pos, new Player(playerId));
                } else if (c >= 'a' && c <= 'z') {
                    boxCharacters.add(c);
                    gameMap.put(pos, new Box(c - 'a'));
                } else {
                    throw new IllegalArgumentException("Unknown character in map: " + c);
                }
            }
        }

        // Validate at least one player
        if (playerCount.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        // Validate box-player mapping
        Set<Integer> playerIds = new HashSet<>();
        for (Character pc : playerCount.keySet()) {
            playerIds.add(pc - 'A');
        }

        for (Character bc : boxCharacters) {
            int boxPlayerId = bc - 'a';
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references invalid player ID: " + boxPlayerId);
            }
        }

        // Validate number of destinations equals number of boxes
        int boxCount = 0;
        for (Position pos : gameMap.keySet()) {
            if (gameMap.get(pos) instanceof Box) {
                boxCount++;
            }
        }
        if (boxCount != destinations.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate closed boundary
        validateClosedBoundary(gameMap, width, height);

        return new GameMap(gameMap, destinations, undoLimit);
    }

    private static void validateClosedBoundary(Map<Position, Entity> map, int width, int height) {
        // Check top row
        for (int x = 0; x < width; x++) {
            Position pos = new Position(x, 0);
            Entity e = map.get(pos);
            if (!(e instanceof Wall)) {
                throw new IllegalArgumentException("Closed boundary violated at top row, x=" + x);
            }
        }
        // Check bottom row
        for (int x = 0; x < width; x++) {
            Position pos = new Position(x, height - 1);
            Entity e = map.get(pos);
            if (!(e instanceof Wall)) {
                throw new IllegalArgumentException("Closed boundary violated at bottom row, x=" + x);
            }
        }
        // Check left column
        for (int y = 0; y < height; y++) {
            Position pos = new Position(0, y);
            Entity e = map.get(pos);
            if (!(e instanceof Wall)) {
                throw new IllegalArgumentException("Closed boundary violated at left column, y=" + y);
            }
        }
        // Check right column
        for (int y = 0; y < height; y++) {
            Position pos = new Position(width - 1, y);
            Entity e = map.get(pos);
            if (!(e instanceof Wall)) {
                throw new IllegalArgumentException("Closed boundary violated at right column, y=" + y);
            }
        }
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        // Create a new copy since map is unmodifiable
        throw new NotImplementedException();
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

    // Package-private method for GameState to access the map
    Map<Position, Entity> getEntityMapInternal() {
        return map;
    }
}
