import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a position in the 2D grid.
 */
class Position {
    private int x;
    private int y;

    public Position() {
    }

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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
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

    @Override
    public String toString() {
        return "Position{" + "x=" + x + ", y=" + y + '}';
    }
}

/**
 * Abstract base class for all entities on the game board.
 */
abstract class Entity {
    public Entity() {
    }
}

/**
 * Represents a wall entity.
 */
class Wall extends Entity {
    public Wall() {
    }
}

/**
 * Represents an empty cell.
 */
class Empty extends Entity {
    public Empty() {
    }
}

/**
 * Represents a player entity.
 */
class Player extends Entity {
    private int id;

    public Player() {
    }

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

/**
 * Represents a box entity associated with a player.
 */
class Box extends Entity {
    private int playerId;

    public Box() {
    }

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}

/**
 * Represents the game map with entities and destinations.
 */
class GameMap {
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
        Set<Position> destinations = new HashSet<>();
        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Integer> playerIds = new HashSet<>();
        Map<Character, Integer> boxPlayerMap = new HashMap<>(); // lowercase letter -> player id

        int height = lines.length - 1; // exclude first line
        int width = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > width) {
                width = lines[i].length();
            }
        }

        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = new Position(x, y);
                switch (ch) {
                    case '#':
                        entityMap.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entityMap.put(pos, new Empty());
                        break;
                    case '.':
                        entityMap.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(ch)) {
                            // Player
                            int playerId = ch - 'A';
                            playerIds.add(playerId);
                            entityMap.put(pos, new Player(playerId));
                        } else if (Character.isLowerCase(ch)) {
                            // Box
                            int boxId = ch - 'a';
                            // Will be matched to player later
                            boxPlayerMap.put(ch, boxId);
                            entityMap.put(pos, new Box(boxId));
                        }
                        break;
                }
            }
        }

        // Validate closed boundary
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (!entityMap.containsKey(pos)) {
                    entityMap.put(pos, new Empty());
                }
            }
        }

        // Check boundary walls
        for (int y = 0; y < height; y++) {
            if (!(entityMap.get(new Position(0, y)) instanceof Wall) ||
                !(entityMap.get(new Position(width - 1, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundaries (walls on left/right edges)");
            }
        }
        for (int x = 0; x < width; x++) {
            if (!(entityMap.get(new Position(x, 0)) instanceof Wall) ||
                !(entityMap.get(new Position(x, height - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundaries (walls on top/bottom edges)");
            }
        }

        // Validate at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        // Count boxes and destinations
        long boxCount = entityMap.values().stream().filter(e -> e instanceof Box).count();
        if (boxCount != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes (" + boxCount + ") must equal number of destinations (" + destinations.size() + ")");
        }

        // Validate box player IDs exist
        for (Entity e : entityMap.values()) {
            if (e instanceof Box) {
                Box b = (Box) e;
                if (!playerIds.contains(b.getPlayerId())) {
                    throw new IllegalArgumentException("Box player ID " + b.getPlayerId() + " does not correspond to any player");
                }
            }
        }

        return new GameMap(entityMap, destinations, undoLimit);
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

    public int getUndoLimit() {
        return undoLimit;
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

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }
}

/**
 * Represents a transition of entities between positions.
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new HashMap<>(moves);
    }

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

/**
 * Represents the dynamic state of the game.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Integer, Position> playerPositions;
    private Map<Position, Entity> currentEntities;
    private Set<Position> destinations;
    private Stack<GameStateTransition> history;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.undoQuota = gameMap.getUndoLimit();
        this.currentEntities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new Stack<>();

        // Copy entities from game map
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity e = gameMap.getEntity(pos);
                if (e != null) {
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        currentEntities.put(pos, new Player(p.getId()));
                        playerPositions.put(p.getId(), pos);
                    } else if (e instanceof Box) {
                        Box b = (Box) e;
                        currentEntities.put(pos, new Box(b.getPlayerId()));
                    } else if (e instanceof Wall) {
                        currentEntities.put(pos, new Wall());
                    } else {
                        currentEntities.put(pos, new Empty());
                    }
                }
            }
        }
    }

    public Position getPlayerPositionById(int id) {
        return playerPositions.get(id);
    }

    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>(playerPositions.values());
    }

    public Entity getEntity(Position position) {
        return currentEntities.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity e = currentEntities.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity movingEntity = currentEntities.remove(from);
        if (movingEntity != null) {
            currentEntities.put(to, movingEntity);
            if (movingEntity instanceof Player) {
                Player p = (Player) movingEntity;
                playerPositions.put(p.getId(), to);
            }
        }
    }

    public void checkpoint() {
        // Save current entity positions as a transition
        GameStateTransition transition = new GameStateTransition();
        for (Map.Entry<Position, Entity> entry : currentEntities.entrySet()) {
            transition.add(entry.getKey(), entry.getKey());
        }
        history.push(transition);
    }

    public void undo() {
        if (history.isEmpty() || undoQuota == 0) {
            return;
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
        GameStateTransition transition = history.pop();
        GameStateTransition reverseTransition = transition.reverse();
        for (Map.Entry<Position, Position> move : reverseTransition.getMoves().entrySet()) {
            Position from = move.getKey();
            Position to = move.getValue();
            Entity e = currentEntities.remove(from);
            if (e != null) {
                currentEntities.put(to, e);
                if (e instanceof Player) {
                    Player p = (Player) e;
                    playerPositions.put(p.getId(), to);
                }
            }
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Map<Position, Entity> getCurrentEntities() {
        return currentEntities;
    }

    public void setCurrentEntities(Map<Position, Entity> currentEntities) {
        this.currentEntities = currentEntities;
    }

    public Stack<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Stack<GameStateTransition> history) {
        this.history = history;
    }
}

/**
 * Abstract base class for actions.
 */
abstract class Action {
    private int initiator;

    public Action() {
    }

    public Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}

/**
 * Action to exit the game.
 */
class Exit extends Action {
    public Exit() {
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Action representing invalid input.
 */
class InvalidInput extends Action {
    private String message;

    public InvalidInput() {
    }

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * Action to undo the last move.
 */
class Undo extends Action {
    public Undo() {
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract base class for move actions.
 */
abstract class Move extends Action {
    public Move() {
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Move down action.
 */
class Down extends Move {
    public Down() {
    }

    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Move left action.
 */
class Left extends Move {
    public Left() {
    }

    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Move right action.
 */
class Right extends Move {
    public Right() {
    }

    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Move up action.
 */
class Up extends Move {
    public Up() {
    }

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Abstract base class for action results.
 */
abstract class ActionResult {
    private Action action;

    public ActionResult() {
    }

    public ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

/**
 * Success action result.
 */
class Success extends ActionResult {
    public Success() {
    }

    public Success(Action action) {
        super(action);
    }
}

/**
 * Failed action result with a reason.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed() {
    }

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * Interface for rendering the game state.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal-based rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();

        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity e = state.getEntity(pos);
                if (e instanceof Wall) {
                    line.append('#');
                } else if (e instanceof Player) {
                    Player p = (Player) e;
                    char c = (char) ('A' + p.getId());
                    line.append(c);
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    char c = (char) ('a' + b.getPlayerId());
                    line.append(c);
                } else if (e instanceof Empty) {
                    if (destinations.contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    line.append('.');
                }
            }
            outputStream.println(line.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

/**
 * Interface for input engines.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal-based input engine using Scanner.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = terminalScanner.nextLine().trim().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        // Player 0: W, A, S, D, R
        // Player 1: K, H, J, L, U
        // Determine player based on input
        int playerId;
        Move move = null;
        boolean isUndo = false;
        switch (input) {
            case "w":
                playerId = 0;
                move = new Up(playerId);
                break;
            case "a":
                playerId = 0;
                move = new Left(playerId);
                break;
            case "s":
                playerId = 0;
                move = new Down(playerId);
                break;
            case "d":
                playerId = 0;
                move = new Right(playerId);
                break;
            case "r":
                playerId = 0;
                isUndo = true;
                break;
            case "k":
                playerId = 1;
                move = new Up(playerId);
                break;
            case "h":
                playerId = 1;
                move = new Left(playerId);
                break;
            case "j":
                playerId = 1;
                move = new Down(playerId);
                break;
            case "l":
                playerId = 1;
                move = new Right(playerId);
                break;
            case "u":
                playerId = 1;
                isUndo = true;
                break;
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (isUndo) {
            return new Undo(playerId);
        } else {
            return move;
        }
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

/**
 * Abstract base class for Sokoban game implementations.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof Undo) {
            Undo undo = (Undo) action;
            int playerId = undo.getInitiator();
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity targetEntity = state.getEntity(nextPos);
            if (targetEntity instanceof Wall) {
                return new Failed(action, "Cannot move into wall.");
            } else if (targetEntity instanceof Player) {
                return new Failed(action, "Cannot move into another player.");
            } else if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindPos);
                if (behindEntity instanceof Wall || behindEntity instanceof Box || behindEntity instanceof Player) {
                    return new Failed(action, "Cannot push box into obstacle.");
                }
                // Push box
                state.move(nextPos, behindPos);
                // Move player
                state.move(currentPos, nextPos);
                // Check if box was pushed onto destination - checkpoint
                if (state.getDestinations().contains(behindPos)) {
                    state.checkpoint();
                }
                return new Success(action);
            } else if (targetEntity instanceof Empty) {
                state.move(currentPos, nextPos);
                return new Success(action);
            } else {
                return new Failed(action, "Unknown entity.");
            }
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action.");
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}

/**
 * Terminal-based Sokoban game implementation.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            }
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }

    public InputEngine getInputEngine() {
        return inputEngine;
    }

    public void setInputEngine(InputEngine inputEngine) {
        this.inputEngine = inputEngine;
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }
}

/**
 * Interface for Sokoban game.
 */
interface SokobanGame {
    void run();
}

/**
 * Utility class for string resources.
 */
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

/**
 * Throw to indicate that the feature is not implemented.
 */
class NotImplementedException extends RuntimeException {
}

/**
 * Thrown when a branch should not be reached.
 */
class ShouldNotReachException extends RuntimeException {

    /**
     * Create a new should not reach exception.
     */
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

/**
 * Factory for creating Sokoban games.
 */
class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @param mapFile map file.
     * @return The Sokoban game.
     * @throws IOException if mapFile cannot be loaded
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