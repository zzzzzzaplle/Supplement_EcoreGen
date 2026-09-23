import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
        int idx = 0;
        // skip empty leading lines
        while (idx < lines.length && lines[idx].trim().isEmpty()) {
            idx++;
        }
        int undoLimit = 0;
        if (idx < lines.length) {
            try {
                undoLimit = Integer.parseInt(lines[idx].trim());
                idx++;
            } catch (NumberFormatException ignored) {
                // first line isn't a number, treat undo limit as 0
            }
        }

        Map<Position, Entity> workingMap = new HashMap<>();
        Set<Position> destinations = new LinkedHashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;
        int destCount = 0;
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; idx < lines.length; idx++, y++) {
            String line = lines[idx];
            // omit blank lines that may exist
            if (line.isEmpty() && idx == lines.length - 1) {
                break;
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                if (c == '#') {
                    workingMap.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    workingMap.put(pos, new Empty());
                    destCount++;
                } else if (c == '.') {
                    workingMap.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    workingMap.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int id = c - 'a';
                    workingMap.put(pos, new Box(id));
                    boxCount++;
                } else if (c == ' ' || c == '\r') {
                    // treat as empty
                    workingMap.put(pos, new Empty());
                } else {
                    // unknown char treated as empty
                    workingMap.put(pos, new Empty());
                }
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }
        }

        // validate closed boundary
        boolean closed = true;
        for (int x = 0; x <= maxX && closed; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            Entity t = workingMap.get(top);
            Entity b = workingMap.get(bottom);
            if (!(t instanceof Wall) || !(b instanceof Wall)) {
                closed = false;
            }
        }
        for (int y = 0; y <= maxY && closed; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            Entity l = workingMap.get(left);
            Entity r = workingMap.get(right);
            if (!(l instanceof Wall) || !(r instanceof Wall)) {
                closed = false;
            }
        }
        if (!closed) {
            throw new IllegalArgumentException("Map boundary is not closed");
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must contain at least one player");
        }
        if (destCount != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        for (Map.Entry<Position, Entity> e : workingMap.entrySet()) {
            if (e.getValue() instanceof Box) {
                Box box = (Box) e.getValue();
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references unknown player id: " + box.getPlayerId());
                }
            }
        }

        return new GameMap(workingMap, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        // we are working with an unmodifiable map internally; provide a copy-on-write behavior
        if (this.map instanceof Collections.UnmodifiableMap) {
            Map<Position, Entity> copy = new HashMap<>(this.map);
            copy.put(position, entity);
            this.map = copy;
        } else {
            this.map.put(position, entity);
        }
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
