import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class GameMap {
    protected Map<Position, Entity> map;
    protected int maxWidth;
    protected int maxHeight;
    protected Set<Position> destinations;
    protected int undoLimit;

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
        String[] lines = mapText.trim().split("\n");
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid undo limit in map file.");
        }
        int maxHeight = lines.length - 1;
        int maxWidth = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > maxWidth) {
                maxWidth = lines[i].length();
            }
        }

        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);
                if (c == '#') {
                    entityMap.put(pos, new Wall());
                } else if (c == '.') {
                    entityMap.put(pos, new Empty());
                } else if (c == '@') {
                    destinations.add(pos);
                    entityMap.put(pos, new Empty());
                } else if (c == '+' || c == '*') {
                    return parseWithSpecialChars(lines, undoLimit, x, y);
                } else if (Character.isUpperCase(c)) {
                    int playerId = c - 'A';
                    entityMap.put(pos, new Player(playerId));
                } else if (Character.isLowerCase(c)) {
                    int playerId = c - 'a';
                    entityMap.put(pos, new Box(playerId));
                }
            }
        }

        return new GameMap(entityMap, destinations, undoLimit);
    }

    private static GameMap parseWithSpecialChars(String[] lines, int undoLimit, int specialX, int specialY) {
        throw new NotImplementedException();
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        throw new NotImplementedException();
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return map.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player)
            .map(e -> ((Player) e.getValue()).getId())
            .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
