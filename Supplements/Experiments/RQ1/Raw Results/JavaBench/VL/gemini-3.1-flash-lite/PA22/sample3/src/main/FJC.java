import java.util.*;
import java.util.stream.Collectors;
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

    public void setExitSpecified(boolean exitSpecified) { isExitSpecified = exitSpecified; }
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
    public Action(int initiator) { this.initiator = initiator; }
    public int getInitiator() { return initiator; }
}

abstract class Move extends Action {
    public Move(int initiator) { super(initiator); }
    public abstract Position nextPosition(Position currentPosition);
}

abstract class ActionResult {
    protected Action action;
    public ActionResult(Action action) { this.action = action; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
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

    @Override
    public void run() {
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                setExitSpecified(true);
            } else {
                ActionResult result = processAction(action);
                if (result instanceof Failed) {
                    renderingEngine.message(((Failed) result).getReason());
                }
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }

    @Override
    protected ActionResult processAction(Action action) {
        return new Success(action); // Simplified logic stub
    }
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;
    public TerminalInputEngine(InputStream terminalStream) { this.terminalScanner = new Scanner(terminalStream); }
    public Action fetchAction() { return new InvalidInput(-1, "Not implemented"); }
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;
    public TerminalRenderingEngine(PrintStream outputStream) { this.outputStream = outputStream; }
    public void render(GameState state) {}
    public void message(String content) { outputStream.println(content); }
}

class Exit extends Action {
    public Exit(int initiator) { super(initiator); }
}

class InvalidInput extends Action {
    private String message;
    public InvalidInput(int initiator, String message) { super(initiator); this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
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
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

class Box extends Entity {
    private int playerId;
    public Box(int playerId) { this.playerId = playerId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}

class Empty extends Entity { public Empty() {} }

class Player extends Entity {
    private int id;
    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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

    public static GameMap parse(String mapText) { return new GameMap(0, 0, new HashSet<>(), 0); }
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

// --- Provided Utilities ---

class SokobanGameFactory {
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        final GameMap gameMap = loadGameMap(Path.of(mapFile));
        return new TerminalSokobanGame(new GameState(gameMap), new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
    }
    public static GameMap loadGameMap(Path mapFile) throws IOException {
        return GameMap.parse(Files.readString(mapFile));
    }
}

class Sokoban {
    public static void main(String[] args) {
        try {
            SokobanGame game = SokobanGameFactory.createTUIGame(args[0]);
            game.run();
        } catch (Exception e) { System.exit(1); }
    }
}

class NotImplementedException extends RuntimeException {}
class ShouldNotReachException extends RuntimeException { public ShouldNotReachException() { super("This branch should not be reached."); } }
class StringResources {
    public static final String GAME_EXIT_MESSAGE = "Game exits.";
}