import java.util.*;
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
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    public GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(map);
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.trim().split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        if (undoLimit == 0) {
            undoLimit = -1;
        }
        List<String> gridLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim().replaceAll("\\s+$", "");
            if (!line.isEmpty()) {
                gridLines.add(line);
            }
        }
        if (gridLines.isEmpty()) {
            throw new IllegalArgumentException("Empty grid");
        }
        int rows = gridLines.size();
        int cols = gridLines.stream().mapToInt(String::length).max().orElse(0);
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Character, Integer> playerMap = new LinkedHashMap<>();
        int playerIdCounter = 0;
        Set<Position> boxPositions = new HashSet<>();
        for (int row = 0; row < rows; row++) {
            String line = gridLines.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                Position pos = Position.of(col, row);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c == '@') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (c == '+' || c == '*') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (c >= 'a' && c <= 'z') {
                    boxPositions.add(pos);
                    char playerChar = (char) ('A' + (c - 'a'));
                    if (playerMap.containsKey(playerChar)) {
                        int pid = playerMap.get(playerChar);
                        map.put(pos, new Box(pid));
                    } else {
                        throw new IllegalArgumentException("Box '" + c + "' references non-existent player '" + playerChar + "'");
                    }
                } else if (c >= 'A' && c <= 'Z') {
                    if (!playerMap.containsKey(c)) {
                        int pid = playerIdCounter++;
                        playerMap.put(c, pid);
                    }
                    map.put(pos, new Player(playerMap.get(c)));
                } else if (c == ' ') {
                    // skip spaces between rows if any
                }
            }
        }
        if (playerMap.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        if (boxPositions.size() != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes must equal number of destinations");
        }
        validateMapClosed(gridLines);
        return new GameMap(map, destinations, undoLimit);
    }

    public static void validateMapClosed(List<String> lines) {
        int rows = lines.size();
        int cols = lines.get(0).length();
        int maxCols = lines.stream().mapToInt(String::length).max().orElse(0);
        for (int c = 0; c < maxCols; c++) {
            char firstLineFirstChar = lines.get(0).charAt(Math.min(c, lines.get(0).length() - 1));
            char lastLineFirstChar = lines.get(rows - 1).charAt(Math.min(c, lines.get(rows - 1).length() - 1));
            if (firstLineFirstChar != '#' || lastLineFirstChar != '#') {
                throw new IllegalArgumentException("Map must have a closed boundary: top/bottom broken");
            }
        }
        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            first:
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c != '#') {
                    break first;
                }
            }
            char first = line.charAt(0);
            char last = line.charAt(line.length() - 1);
            if (first != '#' || last != '#') {
                throw new IllegalArgumentException("Map must have a closed boundary: row " + r + " broken");
            }
        }
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
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
