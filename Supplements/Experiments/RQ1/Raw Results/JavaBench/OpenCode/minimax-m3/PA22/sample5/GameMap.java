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
        this.destinations = new HashSet<>();
        this.maxWidth = 0;
        this.maxHeight = 0;
        this.undoLimit = 0;
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
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        int undoLimit = 0;
        String[] lines = mapText.split("\\r?\\n");
        int idx = 0;
        if (lines.length > 0) {
            try {
                undoLimit = Integer.parseInt(lines[0].trim());
            } catch (NumberFormatException e) {
                undoLimit = 0;
            }
            idx = 1;
        }
        int y = 0;
        int maxX = 0;
        for (int i = idx; i < lines.length; i++) {
            String line = lines[i];
            int x = 0;
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                Position p = Position.of(x, y);
                switch (c) {
                    case '#':
                        map.put(p, new Wall());
                        break;
                    case '@':
                        destinations.add(p);
                        map.put(p, new Empty());
                        break;
                    case '.':
                        map.put(p, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int pid = c - 'A';
                            map.put(p, new Player(pid));
                            playerIds.add(pid);
                        } else if (c >= 'a' && c <= 'z') {
                            int pid = c - 'a';
                            map.put(p, new Box(pid));
                            boxPlayerIds.add(pid);
                        } else if (c == ' ') {
                            map.put(p, new Empty());
                        }
                        break;
                }
                x++;
            }
            if (x > maxX) maxX = x;
            y++;
        }
        int maxHeightVal = y;
        if (playerIds.isEmpty()) {
            throw new RuntimeException("No players found in map.");
        }
        if (destinations.size() != boxPlayerIds.size()) {
            throw new RuntimeException("Number of destinations must equal number of boxes.");
        }
        for (int pid : boxPlayerIds) {
            if (!playerIds.contains(pid)) {
                throw new RuntimeException("Box references invalid player ID: " + pid);
            }
        }
        GameMap result = new GameMap(map, destinations, undoLimit);
        result.maxWidth = maxX;
        result.maxHeight = maxHeightVal;
        return result;
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
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

    public int getUndoLimitValue() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
