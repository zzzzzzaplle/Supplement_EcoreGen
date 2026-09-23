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
        String[] lines = mapText.split("\n", -1);
        int undoLimit = -1;
        int startIndex = 0;
        if (lines.length > 0) {
            try {
                undoLimit = Integer.parseInt(lines[0].trim());
                startIndex = 1;
            } catch (NumberFormatException e) {
                startIndex = 0;
            }
        }
        int maxWidth = 0;
        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }
        int maxHeight = lines.length - startIndex;
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        List<Box> boxes = new ArrayList<>();
        for (int y = 0; y < maxHeight; y++) {
            String line = lines[y + startIndex];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                } else if (c == '.') {
                    // empty space
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    map.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    Box box = new Box(playerId);
                    map.put(pos, box);
                    boxes.add(box);
                }
            }
        }
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one player.");
        }
        if (destinations.size() != boxes.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (Box box : boxes) {
            if (!playerIds.contains(box.getPlayerId())) {
                throw new IllegalArgumentException("Box references invalid player ID: " + box.getPlayerId());
            }
        }
        for (int x = 0; x < maxWidth; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxHeight - 1);
            if (!(map.get(top) instanceof Wall) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed.");
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxWidth - 1, y);
            if (!(map.get(left) instanceof Wall) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed.");
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        if (map == null) {
            return null;
        }
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (map != null) {
            map.put(position, entity);
        }
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        if (map != null) {
            for (Entity e : map.values()) {
                if (e instanceof Player) {
                    ids.add(((Player) e).getId());
                }
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
