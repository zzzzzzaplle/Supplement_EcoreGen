import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;
import java.net.URL;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

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

// --- SokobanGame Implementation Classes ---
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) { this.state = gameState; }
    protected boolean shouldStop() { return isExitSpecified || state.isWin(); }
    protected abstract ActionResult processAction(Action action);

    public void setExitSpecified(boolean isExitSpecified) { this.isExitSpecified = isExitSpecified; }
    public boolean getIsExitSpecified() { return isExitSpecified; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
}

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() { super(null); }
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                setExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
            } else {
                ActionResult result = processAction(action);
                if (result instanceof Failed) renderingEngine.message(((Failed) result).getReason());
            }
        }
        if (state.isWin()) renderingEngine.message(StringResources.WIN_MESSAGE);
    }

    protected ActionResult processAction(Action action) {
        // Implementation logic for processing actions
        return new Success(action);
    }

    public InputEngine getInputEngine() { return inputEngine; }
    public void setInputEngine(InputEngine inputEngine) { this.inputEngine = inputEngine; }
    public RenderingEngine getRenderingEngine() { return renderingEngine; }
    public void setRenderingEngine(RenderingEngine renderingEngine) { this.renderingEngine = renderingEngine; }
}

// --- Input/Rendering Engines ---
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;
    public TerminalInputEngine() { this.terminalScanner = new Scanner(System.in); }
    public TerminalInputEngine(InputStream terminalStream) { this.terminalScanner = new Scanner(terminalStream); }
    public Action fetchAction() {
        String input = terminalScanner.nextLine().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) return new Exit(-1);
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;
    public TerminalRenderingEngine() { this.outputStream = System.out; }
    public TerminalRenderingEngine(PrintStream outputStream) { this.outputStream = outputStream; }
    public void render(GameState state) { /* Logic to render using characters */ }
    public void message(String content) { outputStream.println(content); }
    public PrintStream getOutputStream() { return outputStream; }
    public void setOutputStream(PrintStream outputStream) { this.outputStream = outputStream; }
}

// --- Actions ---
abstract class Action {
    protected int initiator;
    public Action() {}
    public Action(int initiator) { this.initiator = initiator; }
    public int getInitiator() { return initiator; }
    public void setInitiator(int initiator) { this.initiator = initiator; }
}

class Exit extends Action { public Exit(int initiator) { super(initiator); } public Exit() {} }
class Undo extends Action { public Undo(int initiator) { super(initiator); } public Undo() {} }
class InvalidInput extends Action {
    private String message;
    public InvalidInput(int initiator, String message) { super(initiator); this.message = message; }
    public InvalidInput() {}
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

abstract class Move extends Action {
    public Move() {}
    public Move(int initiator) { super(initiator); }
    public abstract Position nextPosition(Position currentPosition);
}

class Down extends Move { public Down(int initiator) { super(initiator); } public Down() {} public Position nextPosition(Position p) { return Position.of(p.x(), p.y() + 1); } }
class Left extends Move { public Left(int initiator) { super(initiator); } public Left() {} public Position nextPosition(Position p) { return Position.of(p.x() - 1, p.y()); } }
class Right extends Move { public Right(int initiator) { super(initiator); } public Right() {} public Position nextPosition(Position p) { return Position.of(p.x() + 1, p.y()); } }
class Up extends Move { public Up(int initiator) { super(initiator); } public Up() {} public Position nextPosition(Position p) { return Position.of(p.x(), p.y() - 1); } }

// --- ActionResult ---
abstract class ActionResult {
    protected Action action;
    public ActionResult() {}
    public ActionResult(Action action) { this.action = action; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
}

class Success extends ActionResult { public Success(Action action) { super(action); } public Success() {} }
class Failed extends ActionResult {
    private String reason;
    public Failed() {}
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

// --- Entities ---
abstract class Entity { public Entity() {} }
class Box extends Entity {
    private int playerId;
    public Box(int playerId) { this.playerId = playerId; }
    public Box() {}
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}
class Empty extends Entity { public Empty() {} }
class Player extends Entity {
    private int id;
    public Player(int id) { this.id = id; }
    public Player() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
class Wall extends Entity { public Wall() {} }

// --- Supporting Classes ---
class Position {
    private int x, y;
    public Position(int x, int y) { this.x = x; this.y = y; }
    public Position() {}
    public int x() { return x; }
    public int y() { return y; }
    public static Position of(int x, int y) { return new Position(x, y); }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
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

    public GameMap() {}
    public static GameMap parse(String mapText) { return new GameMap(); /* impl */ }
    public Entity getEntity(Position p) { return map.get(p); }
    public void putEntity(Position p, Entity e) { map.put(p, e); }
    public Set<Position> getDestinations() { return destinations; }
    public Optional<Integer> getUndoLimit() { return Optional.of(undoLimit); }
    public Set<Integer> getPlayerIds() { return new HashSet<>(); }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMap(Map<Position, Entity> map) { this.map = map; }
    public void setMaxWidth(int w) { this.maxWidth = w; }
    public void setMaxHeight(int h) { this.maxHeight = h; }
    public void setDestinations(Set<Position> dest) { this.destinations = dest; }
    public void setUndoLimit(int limit) { this.undoLimit = limit; }
}

class GameState {
    private int boardWidth, boardHeight, undoQuota;
    public GameState() {}
    public GameState(GameMap map) {}
    public Position getPlayerPositionById(int id) { return null; }
    public Set<Position> getAllPlayerPositions() { return null; }
    public Entity getEntity(Position p) { return null; }
    public Set<Position> getDestinations() { return null; }
    public boolean isWin() { return false; }
    public void move(Position from, Position to) {}
    public void checkpoint() {}
    public void undo() {}
    public int getMapMaxWidth() { return boardWidth; }
    public int getMapMaxHeight() { return boardHeight; }
    public int getBoardWidth() { return boardWidth; }
    public void setBoardWidth(int w) { boardWidth = w; }
    public int getBoardHeight() { return boardHeight; }
    public void setBoardHeight(int h) { boardHeight = h; }
    public int getUndoQuota() { return undoQuota; }
    public void setUndoQuota(int q) { undoQuota = q; }
}

class GameStateTransition {
    private Map<Position, Position> moves = new HashMap<>();
    public GameStateTransition() {}
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
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }
}

// --- Verbatim Utilities ---
class SokobanGameFactory {
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            final URL resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try { file = Path.of(resource.toURI()); } catch (URISyntaxException e) { throw new RuntimeException("Error loading map:" + mapFile); }
        } else { file = Path.of(mapFile); }
        final GameMap gameMap = loadGameMap(file);
        return new TerminalSokobanGame(new GameState(gameMap), new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
    }
    public static GameMap loadGameMap(Path mapFile) throws IOException {
        final String fileContent = Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }
}

class Sokoban {
    public static void main(String[] args) {
        if (args.length < 1) { System.err.println("Map is not provided."); System.exit(1); }
        final String mapFile = args[0];
        try { final SokobanGame game = SokobanGameFactory.createTUIGame(mapFile); game.run(); } 
        catch (IOException e) { System.err.println("Failed to load game map: " + e); System.exit(1); }
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