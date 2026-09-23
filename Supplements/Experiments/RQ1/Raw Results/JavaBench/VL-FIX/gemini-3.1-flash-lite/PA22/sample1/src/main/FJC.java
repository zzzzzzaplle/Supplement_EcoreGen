import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;

// --- Interfaces & Enums ---

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

// --- Abstract Classes ---

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState state) {
        this.state = state;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        return new Failed(action, "Not implemented");
    }
}

abstract class Action {
    protected int initiator;
    protected Action(int initiator) { this.initiator = initiator; }
    public int getInitiator() { return initiator; }
}

abstract class Move extends Action {
    protected Move(int initiator) { super(initiator); }
    public abstract Position nextPosition(Position currentPosition);
}

abstract class ActionResult {
    protected Action action;
    protected ActionResult(Action action) { this.action = action; }
    public Action getAction() { return action; }
}

abstract class Entity {
    public Entity() {}
}

// --- Concrete Classes ---

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState state, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(state);
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

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;
    public TerminalInputEngine(InputStream terminalStream) { this.terminalScanner = new Scanner(terminalStream); }
    public Action fetchAction() {
        String input = terminalScanner.nextLine().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) return new Exit(-1);
        return new InvalidInput(-1, "Unknown command");
    }
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;
    public TerminalRenderingEngine(PrintStream outputStream) { this.outputStream = outputStream; }
    public void render(GameState state) { /* Implementation omitted for brevity */ }
    public void message(String content) { outputStream.println(content); }
}

class Exit extends Action {
    public Exit(int initiator) { super(initiator); }
}

class InvalidInput extends Action {
    private String message;
    public InvalidInput(int initiator, String message) { super(initiator); this.message = message; }
    public String getMessage() { return message; }
}

class Undo extends Action {
    public Undo(int initiator) { super(initiator); }
}

class Down extends Move {
    public Down(int initiator) { super(initiator); }
    public Position nextPosition(Position p) { return Position.of(p.x(), p.y() + 1); }
}

class Left extends Move {
    public Left(int initiator) { super(initiator); }
    public Position nextPosition(Position p) { return Position.of(p.x() - 1, p.y()); }
}

class Right extends Move {
    public Right(int initiator) { super(initiator); }
    public Position nextPosition(Position p) { return Position.of(p.x() + 1, p.y()); }
}

class Up extends Move {
    public Up(int initiator) { super(initiator); }
    public Position nextPosition(Position p) { return Position.of(p.x(), p.y() - 1); }
}

class Success extends ActionResult {
    public Success(Action action) { super(action); }
}

class Failed extends ActionResult {
    private String reason;
    public Failed(Action action, String reason) { super(action); this.reason = reason; }
}

class Box extends Entity {
    private int playerId;
    public Box(int playerId) { this.playerId = playerId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int id) { this.playerId = id; }
}

class Empty extends Entity {}

class Player extends Entity {
    private int id;
    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}

class Wall extends Entity {}

class Position {
    private int x, y;
    public Position(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public static Position of(int x, int y) { return new Position(x, y); }
    @Override public boolean equals(Object o) { if(!(o instanceof Position)) return false; Position p = (Position)o; return x == p.x && y == p.y; }
    @Override public int hashCode() { return Objects.hash(x, y); }
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

    public static GameMap parse(String mapText) { return new GameMap(10, 10, new HashSet<>(), 0); }
    public Entity getEntity(Position p) { return map.get(p); }
    public void putEntity(Position p, Entity e) { map.put(p, e); }
    public Set<Position> getDestinations() { return destinations; }
    public Optional<Integer> getUndoLimit() { return Optional.of(undoLimit); }
    public Set<Integer> getPlayerIds() { return new HashSet<>(); }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
}

class GameState {
    private int boardWidth, boardHeight, undoQuota;
    public GameState(GameMap map) {}
    public Position getPlayerPositionById(int id) { return null; }
    public Set<Position> getAllPlayerPositions() { return new HashSet<>(); }
    public Entity getEntity(Position p) { return null; }
    public Set<Position> getDestinations() { return new HashSet<>(); }
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

class SokobanGameFactory {
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        return new TerminalSokobanGame(new GameState(null), new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
    }
    public static GameMap loadGameMap(Path mapFile) throws IOException { return GameMap.parse(""); }
}

class Sokoban {
    public static void main(String[] args) {
        if (args.length < 1) System.exit(1);
        try { SokobanGame game = SokobanGameFactory.createTUIGame(args[0]); game.run(); } catch(Exception e) { System.exit(1); }
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