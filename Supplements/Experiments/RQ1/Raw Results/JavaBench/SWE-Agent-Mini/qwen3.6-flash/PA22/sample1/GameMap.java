import java.util.*;

public class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;
    private boolean modifiable;

    public GameMap() {
    }

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
        this.modifiable = true;
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(map);
        this.destinations = destinations;
        this.undoLimit = undoLimit;
        this.modifiable = true;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map must have at least two lines");
        }
        int undoLimit = Integer.parseInt(lines[0].trim());
        List<String> mapLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (!lines[i].trim().isEmpty()) {
                mapLines.add(lines[i].trim());
            }
        }

        int maxHeight = mapLines.size();
        int maxWidth = 0;
        for (String line : mapLines) {
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Map<Integer, List<Position>> boxPlayers = new HashMap<>();

        for (int y = 0; y < maxHeight; y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c == '@') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    playerIds.add(playerId);
                    map.put(pos, new Player(playerId));
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    boxPlayers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(pos);
                    map.put(pos, new Box(playerId));
                }
            }
        }

        // Validate: boxes must reference valid player IDs
        for (int playerId : boxPlayers.keySet()) {
            if (!playerIds.contains(playerId)) {
                throw new IllegalArgumentException("Box references player " + (char)('a' + playerId) + " which does not exist");
            }
        }

        // Validate: number of destinations equals number of boxes
        long boxCount = boxPlayers.values().stream().mapToLong(List::size).sum();
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate: closed boundary
        for (int x = 0; x < maxWidth; x++) {
            Entity top = map.get(Position.of(x, 0));
            Entity bottom = map.get(Position.of(x, maxHeight - 1));
            if (top instanceof Empty || top instanceof Player || top instanceof Box) {
                throw new IllegalArgumentException("Top boundary is not closed");
            }
            if (bottom instanceof Empty || bottom instanceof Player || bottom instanceof Box) {
                throw new IllegalArgumentException("Bottom boundary is not closed");
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            Entity left = map.get(Position.of(0, y));
            Entity right = map.get(Position.of(maxWidth - 1, y));
            if (left instanceof Empty || left instanceof Player || left instanceof Box) {
                throw new IllegalArgumentException("Left boundary is not closed");
            }
            if (right instanceof Empty || right instanceof Player || right instanceof Box) {
                throw new IllegalArgumentException("Right boundary is not closed");
            }
        }

        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must contain at least one player");
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
        if (entity instanceof Wall || entity instanceof Player || entity instanceof Box) {
            if (position.x() >= maxWidth) {
                this.maxWidth = position.x() + 1;
            }
            if (position.y() >= maxHeight) {
                this.maxHeight = position.y() + 1;
            }
        }
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
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
}
