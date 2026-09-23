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
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }

        int undoLimit = Integer.parseInt(lines[0].trim());

        Map<Position, Entity> newMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();

        int maxWidth = 0;
        int maxHeight = 0;

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            if (line == null || line.isEmpty()) {
                continue;
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y - 1);

                switch (c) {
                    case '#':
                        newMap.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        newMap.put(pos, new Empty());
                        break;
                    case '.':
                        newMap.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int playerId = c - 'A';
                            playerIds.add(playerId);
                            newMap.put(pos, new Player(playerId));
                        } else if (Character.isLowerCase(c)) {
                            int playerId = c - 'a';
                            newMap.put(pos, new Box(playerId));
                        }
                }
            }
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }

        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one player must exist");
        }

        // Count boxes and destinations
        int boxCount = 0;
        for (Entity entity : newMap.values()) {
            if (entity instanceof Box) {
                boxCount++;
            }
        }

        if (boxCount != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes must equal number of destinations");
        }

        for (Entity entity : newMap.values()) {
            if (entity instanceof Box) {
                Box box = (Box) entity;
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references invalid player ID");
                }
            }
        }

        // Validate closed boundary
        maxHeight = newMap.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
        for (int x = 0; x < maxWidth; x++) {
            if (!newMap.containsKey(Position.of(x, 0)) || !(newMap.get(Position.of(x, 0)) instanceof Wall)) {
                throw new IllegalArgumentException("Top boundary is not closed");
            }
            if (!newMap.containsKey(Position.of(x, maxHeight - 1)) || !(newMap.get(Position.of(x, maxHeight - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Bottom boundary is not closed");
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            if (!newMap.containsKey(Position.of(0, y)) || !(newMap.get(Position.of(0, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Left boundary is not closed");
            }
            if (!newMap.containsKey(Position.of(maxWidth - 1, y)) || !(newMap.get(Position.of(maxWidth - 1, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Right boundary is not closed");
            }
        }

        return new GameMap(newMap, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        ((HashMap<Position, Entity>) map).put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Set<Position> getMapKeySet() {
        return map.keySet();
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> playerIds = new HashSet<>();
        for (Entity entity : map.values()) {
            if (entity instanceof Player) {
                playerIds.add(((Player) entity).getId());
            }
        }
        return playerIds;
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

    public int getUndoLimitValue() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}
