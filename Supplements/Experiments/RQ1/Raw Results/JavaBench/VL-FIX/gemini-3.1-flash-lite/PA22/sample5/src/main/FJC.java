import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;

// --- Interfaces and Abstract Classes ---

interface SokobanGame {
    void run();
}

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected abstract ActionResult processAction(Action action);

    public void setExitSpecified(boolean exit) { isExitSpecified = exit; }
}

interface InputEngine {
    Action fetchAction();
}

interface RenderingEngine {
    void render(GameState state);
    void message(String content);
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
}

abstract class Entity {
    public Entity() {}
}

// --- Concrete Classes ---

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                setExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
            } else {
                ActionResult result = processAction(action);
                renderingEngine.render(state);
                if (state.isWin()) renderingEngine.message(StringResources.WIN_MESSAGE);
            }
        }
    }

    @Override
    protected ActionResult processAction(Action action) {
        return new Success(action); // Simplified logic stub
    }

    public InputEngine getInputEngine() { return inputEngine; }
    public void setInputEngine(InputEngine e) { this.inputEngine = e; }
    public RenderingEngine getRenderingEngine() { return renderingEngine; }
    public void setRenderingEngine(RenderingEngine r) { this.renderingEngine = r; }
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;
    public TerminalInputEngine(InputStream is) { this.terminalScanner = new Scanner(is); }
    public Action fetchAction() { return new InvalidInput(-1, "Not implemented"); }
    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner s) { this.terminalScanner = s; }
    public TerminalInputEngine() {}
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;
    public TerminalRenderingEngine(PrintStream os) { this.outputStream = os; }
    public void render(GameState state) {}
    public void message(String content) { outputStream.println(content); }
    public PrintStream getOutputStream() { return outputStream; }
    public void setOutputStream(PrintStream p) { this.outputStream = p; }
    public TerminalRenderingEngine() {}
}

class Exit extends Action {
    public Exit(int initiator) { super(initiator); }
    public Exit() { super(-1); }
}

class InvalidInput extends Action {
    private String message;
    public InvalidInput(int initiator, String message) { super(initiator); this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
    public InvalidInput() { super(-1); }
}

class Undo extends Action {
    public Undo(int initiator) { super(initiator); }
    public Undo() { super(-1); }
}

class Down extends Move {
    public Down(int initiator) { super(initiator); }
    public Down() { super(-1); }
    public Position nextPosition(Position p) { return new Position(p.x(), p.y() + 1); }
}

class Left extends Move {
    public Left(int initiator) { super(initiator); }
    public Left() { super(-1); }
    public Position nextPosition(Position p) { return new Position(p.x() - 1, p.y()); }
}

class Right extends Move {
    public Right(int initiator) { super(initiator); }
    public Right() { super(-1); }
    public Position nextPosition(Position p) { return new Position(p.x() + 1, p.y()); }
}

class Up extends Move {
    public Up(int initiator) { super(initiator); }
    public Up() { super(-1); }
    public Position nextPosition(Position p) { return new Position(p.x(), p.y() - 1); }
}

class Success extends ActionResult {
    public Success(Action action) { super(action); }
    public Success() { super(null); }
}

class Failed extends ActionResult {
    private String reason;
    public Failed(Action action, String reason) { super(action); this.reason = reason; }
    public String getReason() { return reason; }
    public void setReason(String r) { this.reason = r; }
    public Failed() { super(null); }
}

class Box extends Entity {
    private int playerId;
    public Box(int playerId) { this.playerId = playerId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int id) { this.playerId = id; }
    public Box() {}
}

class Empty extends Entity { public Empty() {} }

class Player extends Entity {
    private int id;
    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Player() {}
}

class Wall extends Entity { public Wall() {} }

class Position {
    private int x, y;
    public Position(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public static Position of(int x, int y) { return new Position(x, y); }
    public Position() {}
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

    public static GameMap parse(String mapText) { return new GameMap(10, 10, new HashSet<>(), -1); }
    public Entity getEntity(Position p) { return map.get(p); }
    public void putEntity(Position p, Entity e) { map.put(p, e); }
    public Set<Position> getDestinations() { return destinations; }
    public Optional<Integer> getUndoLimit() { return Optional.of(undoLimit); }
    public Set<Integer> getPlayerIds() { return new HashSet<>(); }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMaxWidth(int w) { this.maxWidth = w; }
    public void setMaxHeight(int h) { this.maxHeight = h; }
    public void setDestinations(Set<Position> d) { this.destinations = d; }
    public void setUndoLimit(int u) { this.undoLimit = u; }
    public void setMap(Map<Position, Entity> m) { this.map = m; }
    public GameMap() {}
}

class GameState {
    private int boardWidth, boardHeight, undoQuota;
    public Position getPlayerPositionById(int id) { return new Position(0, 0); }
    public Set<Position> getAllPlayerPositions() { return new HashSet<>(); }
    public Entity getEntity(Position p) { return new Empty(); }
    public Set<Position> getDestinations() { return new HashSet<>(); }
    public boolean isWin() { return false; }
    public void move(Position from, Position to) {}
    public void checkpoint() {}
    public void undo() {}
    public int getMapMaxWidth() { return boardWidth; }
    public int getMapMaxHeight() { return boardHeight; }
    public int getBoardWidth() { return boardWidth; }
    public void setBoardWidth(int w) { this.boardWidth = w; }
    public int getBoardHeight() { return boardHeight; }
    public void setBoardHeight(int h) { this.boardHeight = h; }
    public int getUndoQuota() { return undoQuota; }
    public void setUndoQuota(int u) { this.undoQuota = u; }
    public GameState() {}
    public GameState(GameMap map) {}
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
        t.setMoves(newMoves);
        return t;
    }
    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> m) { this.moves = m; }
    public GameStateTransition() {}
}

class SokobanGameFactory {
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        return new TerminalSokobanGame(new GameState(), new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
    }
    public static GameMap loadGameMap(Path mapFile) throws IOException { return GameMap.parse(""); }
}

class Sokoban {
    public static void main(String[] args) {
        if (args.length < 1) System.exit(1);
    }
}

class NotImplementedException extends RuntimeException {}

class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() { super("This branch should not be reached."); }
}

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