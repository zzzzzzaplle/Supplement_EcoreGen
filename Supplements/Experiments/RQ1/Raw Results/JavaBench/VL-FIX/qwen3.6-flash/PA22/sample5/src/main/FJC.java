import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interface for the Sokoban game logic.
 */
interface SokobanGame {
    void run();
}

/**
 * Abstract base class for Sokoban game implementations.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified = false;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected abstract ActionResult processAction(Action action);
}

/**
 * Terminal-based Sokoban game implementation.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    protected InputEngine inputEngine;
    protected RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.render(state);
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                isExitSpecified = true;
            } else {
                ActionResult result = processAction(action);
                if (result instanceof Success) {
                    renderingEngine.render(state);
                } else if (result instanceof Failed) {
                    Failed failedResult = (Failed) result;
                    renderingEngine.message(failedResult.reason);
                }
            }
        }

        if (state.isWin()) {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        } else {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }

    @Override
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, StringResources.INVALID_INPUT_MESSAGE);
        } else if (action instanceof Undo) {
            if (!state.canUndo()) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            Player player = getPlayerByAction(action);
            if (player == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            
            Position currentPosition = state.getPlayerPositionById(player.getId());
            Position nextPosition = move.nextPosition(currentPosition);
            
            // Check bounds
            if (nextPosition.x() >= state.getMapMaxWidth() || nextPosition.y() >= state.getMapMaxHeight() ||
                nextPosition.x() < 0 || nextPosition.y() < 0) {
                return new Failed(action, "Out of bounds");
            }

            Entity nextEntity = state.getEntity(nextPosition);
            
            if (nextEntity instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            } else if (nextEntity instanceof Player) {
                return new Failed(action, "Blocked by another player");
            } else if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != player.getId()) {
                    return new Failed(action, "Cannot push another player's box");
                }
                
                // Check space behind the box
                Position behindBoxPosition = move.nextPosition(nextPosition);
                if (behindBoxPosition.x() >= state.getMapMaxWidth() || behindBoxPosition.y() >= state.getMapMaxHeight() ||
                    behindBoxPosition.x() < 0 || behindBoxPosition.y() < 0) {
                    return new Failed(action, "Cannot push box out of bounds");
                }
                
                Entity behindEntity = state.getEntity(behindBoxPosition);
                if (behindEntity instanceof Wall || behindEntity instanceof Player || behindEntity instanceof Box) {
                    return new Failed(action, "Cannot push box into wall, player, or another box");
                }
                
                // Perform the move with checkpoint
                state.moveWithCheckpoint(currentPosition, nextPosition, player.getId());
                return new Success(action);
            } else if (nextEntity instanceof Empty) {
                state.move(currentPosition, nextPosition);
                return new Success(action);
            } else {
                return new Failed(action, "Unexpected entity");
            }
        } else {
            return new Failed(action, "Unknown action type");
        }
    }

    private Player getPlayerByAction(Action action) {
        int initiator = action.getInitiator();
        Set<Integer> playerIds = state.getPlayerIds();
        if (!playerIds.contains(initiator)) {
            return null;
        }
        
        // Find the player entity
        for (Position pos : state.getAllPlayerPositions()) {
            Entity entity = state.getEntity(pos);
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p.getId() == initiator) {
                    return p;
                }
            }
        }
        return null;
    }
}

/**
 * Interface for input engines.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal input engine.
 */
class TerminalInputEngine implements InputEngine {
    protected Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (terminalScanner.hasNextLine()) {
            String line = terminalScanner.nextLine().trim();
            if (line.isEmpty()) {
                return new InvalidInput(-1, "Empty input");
            }
            
            char firstChar = line.charAt(0);
            
            // Check for exit commands
            if (line.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || line.equalsIgnoreCase("quit")) {
                return new Exit(-1);
            }
            
            if (line.length() == 1) {
                return parseMoveOrUndo(firstChar);
            } else {
                return new InvalidInput(-1, "Invalid input length");
            }
        }
        return new InvalidInput(-1, "No input available");
    }

    private Action parseMoveOrUndo(char c) {
        // Player 0: W (up), A (left), S (down), D (right), R (undo)
        // Player 1: K (up), H (left), J (down), L (right), U (undo)
        
        if (c == 'R' || c == 'U') {
            return new Undo(c == 'R' ? 0 : 1);
        }
        
        // Determine player ID based on key
        int playerId;
        Move move;
        
        if (c == 'W') { playerId = 0; move = new Up(0); }
        else if (c == 'A') { playerId = 0; move = new Left(0); }
        else if (c == 'S') { playerId = 0; move = new Down(0); }
        else if (c == 'D') { playerId = 0; move = new Right(0); }
        else if (c == 'K') { playerId = 1; move = new Up(1); }
        else if (c == 'H') { playerId = 1; move = new Left(1); }
        else if (c == 'J') { playerId = 1; move = new Down(1); }
        else if (c == 'L') { playerId = 1; move = new Right(1); }
        else {
            return new InvalidInput(-1, "Invalid input: " + c);
        }
        
        return move;
    }
}

/**
 * Interface for rendering engines.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    protected PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity == null) {
                    outputStream.print(" ");
                } else if (entity instanceof Wall) {
                    outputStream.print("#");
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    outputStream.print((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    outputStream.print((char) ('A' + player.getId()));
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        outputStream.print("@");
                    } else {
                        outputStream.print(".");
                    }
                } else {
                    outputStream.print("?");
                }
            }
            outputStream.println();
        }
        
        Set<Integer> playerIds = state.getPlayerIds();
        Optional<Integer> undoLimit = state.getUndoLimit();
        StringBuilder info = new StringBuilder();
        info.append("Players: ");
        for (int i = 0; i < playerIds.size(); i++) {
            if (i > 0) info.append(", ");
            info.append((char) ('A' + playerIds.stream().sorted().collect(Collectors.toList()).get(i)));
        }
        
        if (undoLimit.isPresent()) {
            int limit = undoLimit.get();
            if (limit == 0) {
                info.append(" | Undo: Not allowed");
            } else if (limit == -1) {
                info.append(" | Undo: Unlimited");
            } else {
                info.append(" | Undo: ").append(limit);
            }
        }
        
        outputStream.println(info.toString());
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

/**
 * Abstract class representing a game action.
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
 * Action to exit the game.
 */
class Exit extends Action {
    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Action representing invalid input.
 */
class InvalidInput extends Action {
    protected String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

/**
 * Action to undo the last move.
 */
class Undo extends Action {
    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract class representing a movement action.
 */
abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Move down action.
 */
class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Move left action.
 */
class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Move right action.
 */
class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Move up action.
 */
class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Abstract class representing the result of an action.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }
}

/**
 * Result indicating success.
 */
class Success extends ActionResult {
    public Success(Action action) {
        super(action);
    }
}

/**
 * Result indicating failure.
 */
class Failed extends ActionResult {
    protected String reason;

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

/**
 * Abstract class for game entities.
 */
abstract class Entity {
}

/**
 * Box entity.
 */
class Box extends Entity {
    protected int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}

/**
 * Empty space entity.
 */
class Empty extends Entity {
}

/**
 * Player entity.
 */
class Player extends Entity {
    protected int id;

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
 * Represents a 2D position.
 */
class Position {
    protected int x;
    protected int y;

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
 * Represents the game map.
 */
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
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        int maxY = -1;
        int maxX = -1;

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = Position.of(x, y - 1);
                
                if (x > maxX) maxX = x;
                if (y - 1 > maxY) maxY = y - 1;

                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    map.put(pos, new Empty());
                    destinations.add(pos);
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (Character.isUpperCase(c)) {
                    int playerId = c - 'A';
                    map.put(pos, new Player(playerId));
                    playerIds.add(playerId);
                } else if (Character.isLowerCase(c)) {
                    int playerId = c - 'a';
                    map.put(pos, new Box(playerId));
                    boxPlayerIds.add(playerId);
                }
            }
        }

        // Validate map
        if (!playerIds.isEmpty()) {
            if (!playerIds.containsAll(boxPlayerIds)) {
                throw new IllegalArgumentException("Boxes reference invalid player IDs");
            }
            if (playerIds.size() > 26) {
                throw new IllegalArgumentException("Too many players");
            }
        } else {
            throw new IllegalArgumentException("No players in map");
        }

        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Check closed boundary
        for (int y = 0; y <= maxY; y++) {
            Position left = Position.of(0, y);
            Position right = Position.of(maxX, y);
            if (!map.containsKey(left) || !(map.get(left) instanceof Wall) ||
                !map.containsKey(right) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary");
            }
        }
        for (int x = 0; x <= maxX; x++) {
            Position top = Position.of(x, 0);
            Position bottom = Position.of(x, maxY);
            if (!map.containsKey(top) || !(map.get(top) instanceof Wall) ||
                !map.containsKey(bottom) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary");
            }
        }

        GameMap gameMap = new GameMap(map, destinations, undoLimit);
        return gameMap;
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

/**
 * Represents the game state.
 */
class GameState {
    protected GameMap map;
    protected int boardWidth;
    protected int boardHeight;
    protected int undoQuota;
    protected List<GameStateTransition> history;
    protected List<Integer> checkpointIndices;
    protected int currentCheckpointIndex;

    public GameState(GameMap map) {
        this.map = map;
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.undoQuota = map.getUndoLimit().orElse(0);
        this.history = new ArrayList<>();
        this.checkpointIndices = new ArrayList<>();
        this.currentCheckpointIndex = -1;
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : map.map.entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player player = (Player) entry.getValue();
                if (player.getId() == id) {
                    return entry.getKey();
                }
            }
        }
        throw new NoSuchElementException("Player not found");
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : map.map.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return map.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return map.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : map.getDestinations()) {
            Entity entity = getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        GameStateTransition transition = new GameStateTransition();
        Entity entity = map.getEntity(from);
        map.putEntity(from, new Empty());
        map.putEntity(to, entity);
        transition.add(from, to);
        history.add(transition);
    }

    public void moveWithCheckpoint(Position from, Position to, int playerId) {
        move(from, to);
        
        // Check if a box was pushed
        Entity entityAtTo = map.getEntity(to);
        if (entityAtTo instanceof Box) {
            Box box = (Box) entityAtTo;
            if (box.getPlayerId() == playerId) {
                // This was a box push, add checkpoint
                checkpointIndices.add(history.size() - 1);
                if (undoQuota > 0) {
                    undoQuota--;
                }
            }
        }
    }

    public boolean canUndo() {
        if (undoQuota == 0) {
            return false;
        }
        if (undoQuota == -1 || undoQuota > 0) {
            return !checkpointIndices.isEmpty();
        }
        return false;
    }

    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Cannot undo");
        }

        // Get the last checkpoint
        if (checkpointIndices.isEmpty()) {
            throw new IllegalStateException("No checkpoints to undo");
        }

        int checkpointIndex = checkpointIndices.remove(checkpointIndices.size() - 1);
        
        // Undo all transitions from the checkpoint to the end
        while (history.size() > checkpointIndex + 1) {
            GameStateTransition transition = history.remove(history.size() - 1);
            GameStateTransition reversed = transition.reverse();
            
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                Position from = entry.getKey();
                Position to = entry.getValue();
                Entity entity = map.getEntity(to);
                map.putEntity(to, new Empty());
                map.putEntity(from, entity);
            }
        }
        
        // If undo quota is finite, we don't restore it (it was consumed when checkpoint was created)
        // If undo quota is unlimited, we don't change it
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public Set<Integer> getPlayerIds() {
        return map.getPlayerIds();
    }

    public Optional<Integer> getUndoLimit() {
        return map.getUndoLimit();
    }
}

/**
 * Represents a transition in the game state.
 */
class GameStateTransition {
    protected Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }
}

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