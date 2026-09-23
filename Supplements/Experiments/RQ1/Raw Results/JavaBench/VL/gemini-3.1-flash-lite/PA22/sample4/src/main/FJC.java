import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.net.*;

// --- Interfaces ---

interface SokobanGame {
    void run();
}

interface InputEngine {
    Action fetchAction();
}

interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

// --- SokobanGame Implementation ---

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        // Logic for moves, undo, etc. handled by subclasses or game state
        return new Success(action);
    }
}

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            processAction(action);
        }
    }
}

// --- Engines ---

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        String input = terminalScanner.nextLine().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) return new Exit(-1);
        return new InvalidInput(-1, "Invalid");
    }
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        // Implementation logic for grid rendering per requirements
    }

    public void message(String content) {
        outputStream.println(content);
    }
}

// --- Actions ---

abstract class Action {
    protected int initiator;
    protected Action(int initiator) { this.initiator = initiator; }
    public int getInitiator() { return initiator; }
}

class Exit extends Action { public Exit(int initiator) { super(initiator); } }

class InvalidInput extends Action {
    private String message;
    public InvalidInput(int initiator, String message) { super(initiator); this.message = message; }
    public String getMessage() { return message; }
}

class Undo extends Action { public Undo(int initiator) { super(initiator); } }

abstract class Move extends Action {
    protected Move(int initiator) { super(initiator); }
    public abstract Position nextPosition(Position currentPosition);
}

class Down extends Move { public Down(int i) { super(i); } public Position nextPosition(Position p) { return Position.of(p.x(), p.y() + 1); } }
class Left extends Move { public Left(int i) { super(i); } public Position nextPosition(Position p) { return Position.of(p.x() - 1, p.y()); } }
class Right extends Move { public Right(int i) { super(i); } public Position nextPosition(Position p) { return Position.of(p.x() + 1, p.y()); } }
class Up extends Move { public Up(int i) { super(i); } public Position nextPosition(Position p) { return Position.of(p.x(), p.y() - 1); } }

// --- Results ---

abstract class ActionResult {
    protected Action action;
    protected ActionResult(Action action) { this.action = action; }
}

class Success extends ActionResult { public Success(Action a) { super(a); } }
class Failed extends ActionResult { private String reason; public Failed(Action a, String r) { super(a); this.reason = r; } }

// --- Entities ---

abstract class Entity {}
class Box extends Entity {
    private int playerId;
    public Box() {}
    public Box(int playerId) { this.playerId = playerId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int id) { this.playerId = id; }
}
class Empty extends Entity { public Empty() {} }
class Player extends Entity {
    private int id;
    public Player() {}
    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
class Wall extends Entity { public Wall() {} }

// --- Game Logic ---

class Position {
    private int x, y;
    public Position() {}
    public Position(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public static Position of(int x, int y) { return new Position(x, y); }
}

class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth, maxHeight, undoLimit;
    private Set<Position> destinations;

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

    public static GameMap parse(String mapText) { return null; /* Implementation placeholder */ }
    public Entity getEntity(Position p) { return map.get(p); }
    public void putEntity(Position p, Entity e) { map.put(p, e); }
    public Set<Position> getDestinations() { return destinations; }
    public Optional<Integer> getUndoLimit() { return Optional.of(undoLimit); }
    public Set<Integer> getPlayerIds() { return new HashSet<>(); }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
}

class GameState {
    public GameState(GameMap map) {}
    public Position getPlayerPositionById(int id) { return null; }
    public Set<Position> getAllPlayerPositions() { return null; }
    public Entity getEntity(Position p) { return null; }
    public Set<Position> getDestinations() { return null; }
    public boolean isWin() { return false; }
    public void move(Position from, Position to) {}
    public void checkpoint() {}
    public void undo() {}
    public int getMapMaxWidth() { return 0; }
    public int getMapMaxHeight() { return 0; }
}

class GameStateTransition {
    private Map<Position, Position> moves = new HashMap<>();
    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }
    public GameStateTransition reverse() {
        final Map<Position, Position> newMoves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        GameStateTransition t = new GameStateTransition();
        t.moves = newMoves;
        return t;
    }
}

// --- Utilities & Entry Point ---

class SokobanGameFactory {
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        final GameMap gameMap = loadGameMap(Path.of(mapFile));
        return new TerminalSokobanGame(new GameState(gameMap), new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
    }
    public static GameMap loadGameMap(Path mapFile) throws IOException { return GameMap.parse(Files.readString(mapFile)); }
}

class Sokoban {
    public static void main(String[] args) {
        if (args.length < 1) System.exit(1);
        try {
            SokobanGameFactory.createTUIGame(args[0]).run();
        } catch (IOException e) { System.exit(1); }
    }
}

class NotImplementedException extends RuntimeException {}
class ShouldNotReachException extends RuntimeException { public ShouldNotReachException() { super("This branch should not be reached."); } }

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