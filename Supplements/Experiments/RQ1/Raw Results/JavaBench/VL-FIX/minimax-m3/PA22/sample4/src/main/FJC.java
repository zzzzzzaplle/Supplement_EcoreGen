import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Utility for centralized display messages and prompts.
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
 * Thrown when a branch should not be reached. Used to avoid compilation error.
 */
class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

/**
 * Represents 2D grid coordinates.
 */
class Position {
    private int x;
    private int y;

    public Position() {
        this(0, 0);
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

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

/**
 * Abstract entity on the game board.
 */
abstract class Entity {
    public Entity() {
    }
}

/**
 * Represents an empty space on the board.
 */
class Empty extends Entity {
    public Empty() {
        super();
    }
}

/**
 * Represents a wall.
 */
class Wall extends Entity {
    public Wall() {
        super();
    }
}

/**
 * Represents a player.
 */
class Player extends Entity {
    private int id;

    public Player() {
        super();
        this.id = 0;
    }

    public Player(int id) {
        super();
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
 * Represents a box owned by a player.
 */
class Box extends Entity {
    private int playerId;

    public Box() {
        super();
        this.playerId = 0;
    }

    public Box(int playerId) {
        super();
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
 * Abstract base class for all game actions.
 */
abstract class Action {
    protected int initiator;

    public Action() {
        this.initiator = -1;
    }

    protected Action(int initiator) {
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
        super(-1);
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Action representing an invalid input.
 */
class InvalidInput extends Action {
    private String message;

    public InvalidInput() {
        super(-1);
        this.message = "";
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
        super(-1);
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract move action.
 */
abstract class Move extends Action {
    public Move() {
        super(-1);
    }

    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Move down.
 */
class Down extends Move {
    public Down() {
        super(-1);
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
 * Move left.
 */
class Left extends Move {
    public Left() {
        super(-1);
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
 * Move right.
 */
class Right extends Move {
    public Right() {
        super(-1);
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
 * Move up.
 */
class Up extends Move {
    public Up() {
        super(-1);
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
 * Abstract result of performing an action.
 */
abstract class ActionResult {
    protected Action action;

    public ActionResult() {
        this.action = null;
    }

    protected ActionResult(Action action) {
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
 * Successful action result.
 */
class Success extends ActionResult {
    public Success() {
        super();
    }

    public Success(Action action) {
        super(action);
    }
}

/**
 * Failed action result.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed() {
        super();
        this.reason = "";
    }

    public Failed(Action action) {
        super(action);
        this.reason = "";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * Holds a single transition that is reversible.
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
        this.moves = new HashMap<>(moves);
    }
}

/**
 * Static game map definition (parsed from text).
 */
class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {
        this.map = new HashMap<>();
        this.destinations = new HashSet<>();
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
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map");
        }
        int undoLimit = 0;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be undo limit integer", e);
        }

        List<String> body = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            body.add(lines[i]);
        }
        int maxWidth = 0;
        for (String line : body) {
            if (line.length() > maxWidth) maxWidth = line.length();
        }
        int maxHeight = body.size();

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        List<Position> boxPositions = new ArrayList<>();
        Set<Integer> playerIds = new HashSet<>();

        for (int y = 0; y < body.size(); y++) {
            String line = body.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        map.put(pos, new Empty());
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    case ' ':
                        // treat spaces as outside map; ensure boundary is closed
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int id = c - 'A';
                            map.put(pos, new Player(id));
                            playerIds.add(id);
                        } else if (c >= 'a' && c <= 'z') {
                            int id = c - 'a';
                            map.put(pos, new Box(id));
                            boxPositions.add(pos);
                        } else {
                            throw new IllegalArgumentException("Invalid character in map: " + c);
                        }
                        break;
                }
            }
        }

        // Validate: at least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        // Validate: destinations count == boxes count
        if (destinations.size() != boxPositions.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Validate: boxes reference valid player IDs
        for (Position bp : boxPositions) {
            Box b = (Box) map.get(bp);
            if (!playerIds.contains(b.getPlayerId())) {
                throw new IllegalArgumentException("Box references invalid player ID: " + b.getPlayerId());
            }
        }

        // Validate: closed boundary
        if (!isClosedBoundary(map, maxWidth, maxHeight)) {
            throw new IllegalArgumentException("Map boundary is not closed");
        }

        return new GameMap(map, destinations, undoLimit);
    }

    private static boolean isClosedBoundary(Map<Position, Entity> map, int maxWidth, int maxHeight) {
        // Top and bottom rows
        for (int x = 0; x < maxWidth; x++) {
            Entity top = map.get(new Position(x, 0));
            Entity bottom = map.get(new Position(x, maxHeight - 1));
            if (!(top instanceof Wall) || !(bottom instanceof Wall)) {
                return false;
            }
        }
        // Left and right columns
        for (int y = 0; y < maxHeight; y++) {
            Entity left = map.get(new Position(0, y));
            Entity right = map.get(new Position(maxWidth - 1, y));
            if (!(left instanceof Wall) || !(right instanceof Wall)) {
                return false;
            }
        }
        return true;
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        // We need a mutable map for operations; but the field may be unmodifiable
        Map<Position, Entity> mutable = new HashMap<>(this.map);
        mutable.put(position, entity);
        this.map = mutable;
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
    }

    public int getUndoLimit() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public Optional<Integer> getUndoLimitOpt() {
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
}

/**
 * Dynamic game state.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> board;
    private Set<Position> destinations;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> history;
    private Deque<GameStateTransition> checkpoints;

    public GameState() {
        this.board = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
        this.checkpoints = new ArrayDeque<>();
        this.undoQuota = 0;
    }

    public GameState(GameMap gameMap) {
        this();
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.undoQuota = gameMap.getUndoLimit();
        // Copy map
        this.board = new HashMap<>(gameMap.getMap());
        for (Map.Entry<Position, Entity> e : this.board.entrySet()) {
            if (e.getValue() instanceof Player) {
                this.playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
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
        return board.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity e = board.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = board.get(from);
        board.remove(from);
        board.put(to, moving);
        if (moving instanceof Player) {
            playerPositions.put(((Player) moving).getId(), to);
        }
    }

    public void checkpoint() {
        if (!history.isEmpty()) {
            // Merge history into checkpoint stack
            // The spec says checkpoints are recorded when a box is successfully pushed
            // and stored in a history stack.
            // We can push the current accumulated transition as a checkpoint.
            // Implementation: accumulate all moves since last checkpoint into a single transition.
            GameStateTransition combined = new GameStateTransition();
            for (GameStateTransition t : history) {
                combined.getMoves().putAll(t.getMoves());
            }
            checkpoints.push(combined);
            history.clear();
        }
    }

    public void undo() {
        if (checkpoints.isEmpty()) {
            return;
        }
        // Apply checkpoint transitions in reverse atomically
        GameStateTransition last = checkpoints.pop();
        GameStateTransition reversed = last.reverse();
        for (Map.Entry<Position, Position> e : reversed.getMoves().entrySet()) {
            Position from = e.getKey();
            Position to = e.getValue();
            Entity moving = board.get(from);
            board.remove(from);
            board.put(to, moving);
            if (moving instanceof Player) {
                playerPositions.put(((Player) moving).getId(), to);
            }
        }
    }

    public void recordTransition(Position from, Position to) {
        GameStateTransition t = new GameStateTransition();
        t.add(from, to);
        history.push(t);
    }

    public boolean canUndo() {
        return undoQuota == -1 || undoQuota > 0;
    }

    public void consumeUndoQuota() {
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

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
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

    public Map<Position, Entity> getBoard() {
        return board;
    }

    public void setBoard(Map<Position, Entity> board) {
        this.board = board;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public Deque<GameStateTransition> getCheckpoints() {
        return checkpoints;
    }
}

/**
 * Interface for the Sokoban game.
 */
interface SokobanGame {
    void run();
}

/**
 * Abstract base for Sokoban game runners.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = new GameState();
        this.isExitSpecified = false;
    }

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
        if (action instanceof InvalidInput) {
            return new Failed(action);
        }
        if (action instanceof Undo) {
            if (state.canUndo() && !state.getCheckpoints().isEmpty()) {
                state.undo();
                if (state.getUndoQuota() > 0) {
                    state.consumeUndoQuota();
                }
                return new Success(action);
            } else {
                Failed f = new Failed(action);
                f.setReason(StringResources.UNDO_QUOTA_RUN_OUT);
                return f;
            }
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = action.getInitiator();
            Position current = state.getPlayerPositionById(playerId);
            if (current == null) {
                Failed f = new Failed(action);
                f.setReason(StringResources.PLAYER_NOT_FOUND);
                return f;
            }
            Position next = move.nextPosition(current);
            Entity nextEntity = state.getEntity(next);

            if (nextEntity instanceof Wall) {
                Failed f = new Failed(action);
                f.setReason("Wall in the way");
                return f;
            }
            if (nextEntity instanceof Player) {
                Failed f = new Failed(action);
                f.setReason("Another player in the way");
                return f;
            }
            if (nextEntity instanceof Empty) {
                state.move(current, next);
                state.recordTransition(current, next);
                return new Success(action);
            }
            if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != playerId) {
                    Failed f = new Failed(action);
                    f.setReason("Cannot push another player's box");
                    return f;
                }
                Position beyond = move.nextPosition(next);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity instanceof Empty) {
                    // Push box
                    state.move(next, beyond); // box
                    state.move(current, next); // player
                    state.recordTransition(current, next);
                    state.recordTransition(next, beyond);
                    state.checkpoint();
                    return new Success(action);
                } else {
                    Failed f = new Failed(action);
                    f.setReason("Cannot push box");
                    return f;
                }
            }
        }
        return new Failed(action);
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
 * Interface for input engines.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal-based input engine.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this.terminalScanner = new Scanner(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String line = terminalScanner.nextLine().trim();
        if (line.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String lower = line.toLowerCase();
        if (lower.equals("exit") || lower.equals("quit") || lower.equals(StringResources.EXIT_COMMAND_TEXT)) {
            return new Exit(-1);
        }
        // Single character commands
        if (line.length() == 1) {
            char c = line.charAt(0);
            // Player 0: W A S D R
            switch (c) {
                case 'W': case 'w':
                    return new Up(0);
                case 'A': case 'a':
                    return new Left(0);
                case 'S': case 's':
                    return new Down(0);
                case 'D': case 'd':
                    return new Right(0);
                case 'R': case 'r':
                    return new Undo(0);
                // Player 1: K H J L U
                case 'K': case 'k':
                    return new Up(1);
                case 'H': case 'h':
                    return new Left(1);
                case 'J': case 'j':
                    return new Down(1);
                case 'L': case 'l':
                    return new Right(1);
                case 'U': case 'u':
                    return new Undo(1);
                default:
                    return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
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
 * Terminal-based rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
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
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                char ch;
                if (e instanceof Wall) {
                    ch = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    ch = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    ch = (char) ('A' + pl.getId());
                } else {
                    // Empty
                    if (destinations.contains(p)) {
                        ch = '@';
                    } else {
                        ch = '.';
                    }
                }
                sb.append(ch);
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

/**
 * Terminal-based Sokoban game runner.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = new TerminalInputEngine();
        this.renderingEngine = new TerminalRenderingEngine();
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                setExitSpecified(true);
                break;
            }
            ActionResult result = processAction(action);
            if (result instanceof Success) {
                renderingEngine.render(state);
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            } else if (result instanceof Failed) {
                Failed f = (Failed) result;
                renderingEngine.message(f.getReason());
            }
        }
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
 * Factory for creating Sokoban games.
 */
class SokobanGameFactory {

    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
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

    public static GameMap loadGameMap(Path mapFile) throws IOException {
        final String fileContent = Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }
}

/**
 * The holder of the entry point of the game.
 */
class Sokoban {

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