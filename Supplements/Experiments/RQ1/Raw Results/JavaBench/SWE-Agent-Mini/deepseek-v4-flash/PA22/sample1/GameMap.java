import java.util.*;

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
        // First line is undo limit
        int undoLimit = Integer.parseInt(lines[0].trim());
        
        int height = lines.length - 1;
        int width = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > width) {
                width = lines[i].length();
            }
        }
        
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Map<Integer, Integer> boxCount = new HashMap<>();
        
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
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
                    map.put(pos, new Player(playerId));
                    playerIds.add(playerId);
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    map.put(pos, new Box(playerId));
                    boxCount.merge(playerId, 1, Integer::sum);
                    playerIds.add(playerId);
                }
            }
        }
        
        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }
        
        // Validate: number of destinations must equal number of boxes
        int totalBoxes = boxCount.values().stream().mapToInt(Integer::intValue).sum();
        if (destinations.size() != totalBoxes) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        
        // Validate: boxes must reference valid player IDs
        for (Map.Entry<Integer, Integer> entry : boxCount.entrySet()) {
            if (!playerIds.contains(entry.getKey())) {
                throw new IllegalArgumentException("Box references player ID " + entry.getKey() + " which does not exist in the map.");
            }
        }
        
        // Validate: closed boundary - all positions on border must be walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = map.get(pos);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    if (entity == null || !(entity instanceof Wall)) {
                        throw new IllegalArgumentException("Map must have a closed boundary.");
                    }
                }
            }
        }
        
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (map instanceof HashMap) {
            ((HashMap<Position, Entity>) map).put(position, entity);
        } else {
            Map<Position, Entity> mutableMap = new HashMap<>(map);
            mutableMap.put(position, entity);
            map = Collections.unmodifiableMap(mutableMap);
        }
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
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            if (entry.getValue() instanceof Player) {
                ids.add(((Player) entry.getValue()).getId());
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

    public Map<Position, Entity> getMap() {
        return map;
    }

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }
}
