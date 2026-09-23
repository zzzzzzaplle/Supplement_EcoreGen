import java.util.*;
import java.util.stream.Collectors;

class GameMap {

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
        List<String> lines = Arrays.asList(mapText.trim().split("\n"));
        int undoLimit = Integer.parseInt(lines.get(0));
        List<String> mapLines = lines.subList(1, lines.size());

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        List<Player> players = new ArrayList<>();

        // Validate first line is closed boundary
        String firstRow = mapLines.get(0);
        String lastRow = mapLines.get(mapLines.size() - 1);
        if (firstRow.contains("#") || lastRow.contains("#")) {
            throw new IllegalArgumentException("Map must have a closed boundary");
        }

        for (int y = 0; y < mapLines.size(); y++) {
            String line = mapLines.get(y);
            if (line.length() != firstRow.length()) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (x == 0 || x == line.length() - 1 || y == 0 || y == mapLines.size() - 1) {
                    if (c != '#' && c != ' ' && c != '.') {
                        throw new IllegalArgumentException("Boundary must be walls (#)");
                    }
                    // Boundary positions that could contain entities
                    if (c == '#') {
                        map.put(Position.of(x, y), new Wall());
                    }
                    continue;
                }
                switch (c) {
                    case '#':
                        map.put(Position.of(x, y), new Wall());
                        break;
                    case '@':
                        map.put(Position.of(x, y), new Empty());
                        destinations.add(Position.of(x, y));
                        break;
                    case '.':
                        map.put(Position.of(x, y), new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int playerId = c - 'A';
                            Player player = new Player(playerId);
                            players.add(player);
                            map.put(Position.of(x, y), player);
                        } else if (Character.isLowerCase(c)) {
                            int playerId = c - 'a';
                            if (playerId >= 26) {
                                throw new IllegalArgumentException("Too many players, max 26 supported");
                            }
                            Box box = new Box(playerId);
                            map.put(Position.of(x, y), box);
                        } else {
                            throw new IllegalArgumentException("Invalid character: " + c);
                        }
                        break;
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one player");
        }
        if (destinations.size() != map.values().stream().filter(e -> e instanceof Box).count()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate boxes reference valid player IDs that exist in the map
        Set<Integer> playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
        for (Entity entity : map.values()) {
            if (entity instanceof Box) {
                if (!playerIds.contains(((Box) entity).getPlayerId())) {
                    throw new IllegalArgumentException("Box references invalid player ID");
                }
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return this.map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return this.destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(this.undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return this.map.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return this.map;
    }
}
