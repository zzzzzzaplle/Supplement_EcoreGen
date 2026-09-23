import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Interface for the Sokoban Game.
 */
interface SokobanGame {
    void run();
}

/**
 * Abstract base class for Sokoban games.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        
        if (action instanceof Undo) {
            if (state.getUndoQuota() == 0) {
                return new Failed(StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        }

        if (action instanceof Move) {
            Move moveAction = (Move) action;
            int initiator = moveAction.getInitiator();
            
            // Find the player by ID
            Entity entity = null;
            Position playerPos = null;
            for (Position pos : state.getAllPlayerPositions()) {
                Entity e = state.getEntity(pos);
                if (e instanceof Player) {
                    Player p = (Player) e;
                    if (p.getId() == initiator) {
                        entity = p;
                        playerPos = pos;
                        break;
                    }
                }
            }
            
            if (entity == null || playerPos == null) {
                return new Failed(StringResources.PLAYER_NOT_FOUND);
            }
            
            Position nextPos = moveAction.nextPosition(playerPos);
            
            // Check boundaries
            if (nextPos.x() < 0 || nextPos.x() >= state.getMapMaxWidth() ||
                nextPos.y() < 0 || nextPos.y() >= state.getMapMaxHeight()) {
                return new Failed("Out of bounds");
            }
            
            Entity targetEntity = state.getEntity(nextPos);
            
            // Check if target is a wall
            if (targetEntity instanceof Wall) {
                return new Failed("Blocked by wall");
            }
            
            // Check if target is another player
            if (targetEntity instanceof Player) {
                return new Failed("Blocked by player");
            }
            
            // If target is a box
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                // Check if box belongs to the player
                if (box.getPlayerId() != initiator) {
                    return new Failed("Cannot push another player's box");
                }
                
                // Calculate position behind the box
                Position behindBoxPos = moveAction.nextPosition(nextPos);
                
                // Check boundaries for behindBoxPos
                if (behindBoxPos.x() < 0 || behindBoxPos.x() >= state.getMapMaxWidth() ||
                    behindBoxPos.y() < 0 || behindBoxPos.y() >= state.getMapMaxHeight()) {
                    return new Failed("Cannot push box out of bounds");
                }
                
                Entity behindBoxEntity = state.getEntity(behindBoxPos);
                
                // Check if behind box is a wall or player or another box
                if (behindBoxEntity instanceof Wall || behindBoxEntity instanceof Player || behindBoxEntity instanceof Box) {
                    return new Failed("Cannot push box because space behind is blocked");
                }
                
                // Check if behind box is a destination
                boolean isDestination = state.getDestinations().contains(behindBoxPos);
                
                // Perform the move with checkpoint if a box is pushed
                state.checkpoint();
                state.move(playerPos, nextPos);
                state.move(nextPos, behindBoxPos);
                
                // Check if box is on destination
                if (isDestination) {
                    // Check if this completes the game
                    if (state.isWin()) {
                        return new Success(action);
                    }
                }
                
                return new Success(action);
            }
            
            // If target is empty or destination, just move
            state.move(playerPos, nextPos);
            return new Success(action);
        }
        
        return new Failed("Unknown action");
    }
}

/**
 * Terminal Sokoban Game implementation.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof InvalidInput) {
                renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
            } else {
                ActionResult result = processAction(action);
                if (result instanceof Failed) {
                    Failed failedResult = (Failed) result;
                    renderingEngine.message(failedResult.getReason());
                } else {
                    renderingEngine.render(state);
                }
                
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }
}

/**
 * Interface for Input Engine.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal Input Engine implementation.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        if (terminalScanner.hasNext()) {
            String input = terminalScanner.next();
            
            // Check for exit command
            if (input.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT)) {
                return new Exit(-1);
            }
            
            // Parse commands for players
            // Player 0: W (up), A (left), S (down), D (right), R (undo)
            // Player 1: K (up), H (left), J (down), L (right), U (undo)
            if (input.length() == 1) {
                char c = input.charAt(0);
                switch (c) {
                    case 'w': case 'W': return new Up(0);
                    case 'a': case 'A': return new Left(0);
                    case 's': case 'S': return new Down(0);
                    case 'd': case 'D': return new Right(0);
                    case 'r': case 'R': return new Undo(0);
                    case 'k': case 'K': return new Up(1);
                    case 'h': case 'H': return new Left(1);
                    case 'j': case 'J': return new Down(1);
                    case 'l': case 'L': return new Right(1);
                    case 'u': case 'U': return new Undo(1);
                    default: return new InvalidInput(-1, "Invalid command");
                }
            }
        }
        return new InvalidInput(-1, "No input");
    }
}

/**
 * Interface for Rendering Engine.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal Rendering Engine implementation.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        
        // Create a grid to hold the characters
        char[][] grid = new char[height][width];
        
        // Initialize with empty spaces
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }
        
        // Place destinations
        for (Position dest : state.getDestinations()) {
            if (dest.x() >= 0 && dest.x() < width && dest.y() >= 0 && dest.y() < height) {
                grid[dest.y()][dest.x()] = '@';
            }
        }
        
        // Place entities
        for (Position pos : state.getAllPlayerPositions()) {
            Entity entity = state.getEntity(pos);
            if (entity != null && pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char playerChar = (char) ('A' + player.getId());
                    grid[pos.y()][pos.x()] = playerChar;
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char boxChar = (char) ('a' + box.getPlayerId());
                    grid[box.getY()][box.getX()] = boxChar;
                } else if (entity instanceof Wall) {
                    grid[pos.y()][pos.x()] = '#';
                }
            }
        }
        
        // Also check for boxes not in allPlayerPositions (since they might be at different positions now)
        // We need to iterate through the map to get all entities
        // But GameState doesn't expose the map directly, so we rely on getAllPlayerPositions and getEntity
        // This is a limitation, but we'll assume getAllPlayerPositions covers all players
        // For boxes, we need to find them. Since GameState doesn't expose all positions,
        // we might need to iterate through possible positions or maintain a list.
        // For now, let's assume that boxes are also tracked or we iterate through the grid again.
        
        // Actually, let's iterate through the grid positions and get entities
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity != null) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        char playerChar = (char) ('A' + player.getId());
                        grid[y][x] = playerChar;
                    } else if (entity instanceof Box) {
                        Box box = (Box) entity;
                        char boxChar = (char) ('a' + box.getPlayerId());
                        grid[y][x] = boxChar;
                    } else if (entity instanceof Wall) {
                        grid[y][x] = '#';
                    }
                }
            }
        }
        
        // Print the grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputStream.print(grid[y][x]);
            }
            outputStream.println();
        }
    }

    public void message(String content) {
        outputStream.println(content);
    }
}

/**
 * Abstract class for Actions.
 */
abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return initiator;
    }
}

/**
 * Exit action.
 */
class Exit extends Action {
    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Invalid Input action.
 */
class InvalidInput extends Action {
    private String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

/**
 * Undo action.
 */
class Undo extends Action {
    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract class for Move actions.
 */
abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Down move action.
 */
class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Left move action.
 */
class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Right move action.
 */
class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Up move action.
 */
class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Abstract class for Action Results.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }
}

/**
 * Success action result.
 */
class Success extends ActionResult {
    public Success(Action action) {
        super(action);
    }
}

/**
 * Failed action result.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed(String reason) {
        super(null);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

/**
 * Abstract class for Entities.
 */
abstract class Entity {
}

/**
 * Box entity.
 */
class Box extends Entity {
    private int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
    
    // Helper methods to track position
    private int x;
    private int y;
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}

/**
 * Empty entity.
 */
class Empty extends Entity {
}

/**
 * Player entity.
 */
class Player extends Entity {
    private int id;

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

/**
 * Wall entity.
 */
class Wall extends Entity {
}

/**
 * Position class for 2D grid coordinates.
 */
class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

/**
 * GameMap class for managing the game board.
 */
class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(map);
        this.destinations = new HashSet<>(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.trim().split("\\r?\\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Map is empty");
        }
        
        // First line is undo limit
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
        }
        
        // Rest of the lines are the map
        List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
        
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        
        for (int y = 0; y < mapLines.size(); y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    // Check if there's already an entity here
                    Entity existingEntity = map.get(pos);
                    if (existingEntity != null && !(existingEntity instanceof Empty)) {
                        // Destination with existing entity (e.g., box on destination)
                        // We keep the existing entity and add destination
                    } else {
                        map.put(pos, new Empty());
                    }
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (Character.isDigit(c)) {
                    // Player
                    int playerId = Character.getNumericValue(c);
                    playerIds.add(playerId);
                    map.put(pos, new Player(playerId));
                } else if (Character.isLowerCase(c)) {
                    // Box
                    int playerId = c - 'a';
                    boxPlayerIds.add(playerId);
                    Box box = new Box(playerId);
                    box.setPosition(x, y);
                    map.put(pos, box);
                }
            }
        }
        
        // Validate map
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        
        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        
        // Validate box player IDs exist in map
        for (int playerId : boxPlayerIds) {
            if (!playerIds.contains(playerId)) {
                throw new IllegalArgumentException("Box references invalid player ID: " + playerId);
            }
        }
        
        // Check closed boundary
        int width = mapLines.get(0).length();
        int height = mapLines.size();
        
        for (int x = 0; x < width; x++) {
            if (map.get(new Position(x, 0)) == null || map.get(new Position(x, 0)) instanceof Empty) {
                throw new IllegalArgumentException("Map is not closed at top");
            }
            if (map.get(new Position(x, height - 1)) == null || map.get(new Position(x, height - 1)) instanceof Empty) {
                throw new IllegalArgumentException("Map is not closed at bottom");
            }
        }
        
        for (int y = 0; y < height; y++) {
            if (map.get(new Position(0, y)) == null || map.get(new Position(0, y)) instanceof Empty) {
                throw new IllegalArgumentException("Map is not closed at left");
            }
            if (map.get(new Position(width - 1, y)) == null || map.get(new Position(width - 1, y)) instanceof Empty) {
                throw new IllegalArgumentException("Map is not closed at right");
            }
        }
        
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (entity instanceof Box) {
            Box box = (Box) entity;
            box.setPosition(position.x(), position.y());
        }
        map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return new HashSet<>(destinations);
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
    
    public Set<Position> getMapKeySet() {
        return map.keySet();
    }
}

/**
 * GameState class for managing the dynamic game state.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private List<GameStateTransition> checkpointHistory = new ArrayList<>();
    private List<GameStateTransition> currentTransitionHistory = new ArrayList<>();

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(0);
    }

    public Position getPlayerPositionById(int id) {
        for (Position pos : getAllPlayerPositions()) {
            Entity entity = getEntity(pos);
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getId() == id) {
                    return pos;
                }
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Position pos : gameMap.getMapKeySet()) {
            Entity entity = getEntity(pos);
            if (entity instanceof Player) {
                positions.add(pos);
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : getDestinations()) {
            Entity entity = getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = getEntity(from);
        if (entity == null) {
            throw new IllegalStateException("No entity at position: " + from);
        }
        
        // Remove entity from old position
        gameMap.putEntity(from, new Empty());
        
        // Add entity to new position
        gameMap.putEntity(to, entity);
        
        // Update box position if it's a box
        if (entity instanceof Box) {
            Box box = (Box) entity;
            box.setPosition(to.x(), to.y());
        }
        
        // Record transition
        GameStateTransition transition = new GameStateTransition();
        transition.add(from, to);
        currentTransitionHistory.add(transition);
    }

    public void checkpoint() {
        if (!currentTransitionHistory.isEmpty()) {
            // Create a snapshot of the current history
            GameStateTransition checkpoint = new GameStateTransition();
            for (GameStateTransition transition : currentTransitionHistory) {
                checkpoint.add(transition);
            }
            checkpointHistory.add(checkpoint);
            currentTransitionHistory.clear();
        }
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            return;
        }
        
        GameStateTransition checkpoint = checkpointHistory.remove(checkpointHistory.size() - 1);
        GameStateTransition reversed = checkpoint.reverse();
        
        // Apply reversed transitions
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = gameMap.getEntity(to);
            
            // Move entity back
            gameMap.putEntity(to, new Empty());
            gameMap.putEntity(from, entity);
            
            // Update box position if it's a box
            if (entity instanceof Box) {
                Box box = (Box) entity;
                box.setPosition(from.x(), from.y());
            }
        }
        
        // Decrease undo quota
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }
    
    public int getUndoQuota() {
        return undoQuota;
    }
}

/**
 * GameStateTransition class for managing state transitions.
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new LinkedHashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new LinkedHashMap<>(moves);
    }

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }
    
    public void add(GameStateTransition other) {
        for (Map.Entry<Position, Position> entry : other.getMoves().entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    public GameStateTransition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }
    
    public Map<Position, Position> getMoves() {
        return new HashMap<>(moves);
    }
}

// Factory and Utility classes

/**
 * Factory for creating Sokoban games
 */
class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @param mapFile map file.
     * @return The Sokoban game.
     * @throws IOException if mapFile cannot be load
     */
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            final URL resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                file = Path.of(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error loading map:" + mapFile);
            }
        } else {
            file = Path.of(mapFile);
        }
        final GameMap gameMap = loadGameMap(file);
        return new TerminalSokobanGame(
            new GameState(gameMap),
            new TerminalInputEngine(System.in),
            new TerminalRenderingEngine(System.out)
        );
    }
    /**
     * @param mapFile The file containing the game map.
     * @return The parsed game map.
     * @throws IOException When there is an issue loading the file.
     */
    public static GameMap loadGameMap(Path mapFile) throws IOException {
        final String fileContent = Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }
}

/**
 * The holder of the entry point of the game.
 */
class Sokoban {

    /**
     * The entry point of the program.
     *
     * @param args The command line args.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Map is not provided.");
            System.exit(1);
        }
        final String mapFile = args[0];
        try {
            final SokobanGame game = SokobanGameFactory.createTUIGame(mapFile);
            game.run();
        } catch (IOException e) {
            System.err.println("Failed to load game map: " + e);
            System.exit(1);
        }
    }
}

/**
 * Throw to indicate that the feature is not implemented.
 */
class NotImplementedException extends RuntimeException {
}

/**
 * Thrown when a branch should not be reached. Used to avoid compilation error.
 */
class ShouldNotReachException extends RuntimeException {

    /**
     * Create a new should not reach exception.
     */
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

@SuppressWarnings("MissingJavadoc")
class StringResources {

    public static final String GAME_READY_MESSAGE = "Sokoban game is ready.";
    public static final String INVALID_INPUT_MESSAGE = "Invalid Input.";


    public static final String UNDO_QUOTA_TEMPLATE = "Undo Quota: %s";
    public static final String UNDO_QUOTA_UNLIMITED = "Unlimited";
    public static final String UNDO_QUOTA_RUN_OUT = "You have run out of your undo quota.";

    public static final String PLAYER_NOT_FOUND = "Player not found.";

    public static final String GAME_EXIT_MESSAGE = "Game exits.";
    public static final String WIN_MESSAGE = "You win.";

    public static final String EXIT_COMMAND_TEXT = "exit";
}