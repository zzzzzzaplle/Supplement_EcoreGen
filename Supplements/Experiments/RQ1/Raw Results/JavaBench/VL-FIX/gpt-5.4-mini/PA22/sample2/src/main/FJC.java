import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Game entry contract.
 */
interface SokobanGame {
    void run();
}

/**
 * Base game implementation.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action == null) {
            return new Failed("null");
        }
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            try {
                this.state.undo();
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage());
            }
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position from;
            try {
                from = this.state.getPlayerPositionById(initiator);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage());
            }
            Position to = move.nextPosition(from);
            try {
                this.state.move(from, to);
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage());
            }
        }
        return new Failed("Unsupported action.");
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}

/**
 * Terminal-based game runner.
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
        if (renderingEngine == null || inputEngine == null || state == null) {
            return;
        }
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            renderingEngine.render(state);
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE,
                    state.getUndoQuota() < 0 ? StringResources.UNDO_QUOTA_UNLIMITED : state.getUndoQuota()));
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (action instanceof InvalidInput) {
                renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
            }
        }
        if (state != null && state.isWin()) {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        } else {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
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
 * Action input contract.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal input implementation.
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
        if (terminalScanner == null || !terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String input = terminalScanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (input.equalsIgnoreCase("w")) return new Up(0);
        if (input.equalsIgnoreCase("a")) return new Left(0);
        if (input.equalsIgnoreCase("s")) return new Down(0);
        if (input.equalsIgnoreCase("d")) return new Right(0);
        if (input.equalsIgnoreCase("r")) return new Undo(0);
        if (input.equalsIgnoreCase("k")) return new Up(1);
        if (input.equalsIgnoreCase("h")) return new Left(1);
        if (input.equalsIgnoreCase("j")) return new Down(1);
        if (input.equalsIgnoreCase("l")) return new Right(1);
        if (input.equalsIgnoreCase("u")) return new Undo(1);
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

/**
 * Rendering contract.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal rendering implementation.
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
        if (outputStream == null || state == null) {
            return;
        }
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position p = Position.of(x, y);
                Entity entity = state.getEntity(p);
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    line.append((char) ('a' + ((Box) entity).getPlayerId()));
                } else if (entity instanceof Player) {
                    line.append((char) ('A' + ((Player) entity).getId()));
                } else if (state.getDestinations().contains(p)) {
                    line.append('@');
                } else {
                    line.append('.');
                }
            }
            outputStream.println(line);
        }
    }

    @Override
    public void message(String content) {
        if (outputStream != null) {
            outputStream.println(content);
        }
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

/**
 * Base action.
 */
abstract class Action {
    protected int initiator;

    protected Action() {
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

class Exit extends Action {
    public Exit() {
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

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

class Undo extends Action {
    public Undo() {
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

abstract class Move extends Action {
    protected Move() {
    }

    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

class Down extends Move {
    public Down() {
    }

    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}

class Left extends Move {
    public Left() {
    }

    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() - 1, currentPosition.y());
    }
}

class Right extends Move {
    public Right() {
    }

    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}

class Up extends Move {
    public Up() {
    }

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Action result base type.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult() {
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

class Success extends ActionResult {
    public Success() {
    }

    public Success(Action action) {
        super(action);
    }
}

class Failed extends ActionResult {
    private String reason;

    public Failed() {
    }

    public Failed(String reason) {
        this.reason = reason;
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
 * Board entity.
 */
abstract class Entity {
    public Entity() {
    }
}

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

class Empty extends Entity {
    public Empty() {
    }
}

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

class Wall extends Entity {
    public Wall() {
    }
}

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
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position(" + x + "," + y + ")";
    }
}

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
        String[] lines = mapText.replace("\r", "").split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        List<Box> boxes = new ArrayList<>();
        int height = lines.length - 1;
        int width = 0;
        for (int y = 1; y < lines.length; y++) {
            width = Math.max(width, lines[y].length());
        }
        int playerCount = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position p = Position.of(x, y - 1);
                if (c == '#') map.put(p, new Wall());
                else if (c == '.') map.put(p, new Empty());
                else if (c == '@') {
                    map.put(p, new Empty());
                    destinations.add(p);
                } else if (c >= 'A' && c <= 'Z') {
                    map.put(p, new Player(c - 'A'));
                    playerCount++;
                } else if (c >= 'a' && c <= 'z') {
                    Box b = new Box(c - 'a');
                    map.put(p, b);
                    boxes.add(b);
                } else {
                    map.put(p, new Empty());
                }
            }
        }
        return new GameMap(map, destinations, undoLimit);
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
        for (Entity e : map.values()) {
            if (e instanceof Player) ids.add(((Player) e).getId());
            if (e instanceof Box) ids.add(((Box) e).getPlayerId());
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

class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Deque<GameStateTransition> history = new ArrayDeque<>();

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> e : gameMap.getMap().entrySet()) {
            if (e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id) return e.getKey();
        }
        throw new RuntimeException(StringResources.PLAYER_NOT_FOUND);
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> res = new HashSet<>();
        for (Map.Entry<Position, Entity> e : gameMap.getMap().entrySet()) {
            if (e.getValue() instanceof Player) res.add(e.getKey());
        }
        return res;
    }

    public Entity getEntity(Position position) {
        return gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position p : getDestinations()) {
            if (!(getEntity(p) instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = gameMap.getEntity(from);
        Entity target = gameMap.getEntity(to);
        if (!(moving instanceof Player)) throw new RuntimeException("No player at source.");
        if (target == null) {
            gameMap.putEntity(from, new Empty());
            gameMap.putEntity(to, moving);
            return;
        }
        if (target instanceof Empty) {
            gameMap.putEntity(from, new Empty());
            gameMap.putEntity(to, moving);
            return;
        }
        throw new RuntimeException("Blocked.");
    }

    public void checkpoint() {
        history.push(new GameStateTransition());
    }

    public void undo() {
        if (undoQuota == 0) {
            throw new RuntimeException(StringResources.UNDO_QUOTA_RUN_OUT);
        }
        if (history.isEmpty()) {
            throw new RuntimeException("Nothing to undo.");
        }
        GameStateTransition t = history.pop();
        if (undoQuota > 0) undoQuota--;
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

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }
}

class GameStateTransition {
    private Map<Position, Position> moves = new HashMap<>();

    public GameStateTransition() {
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

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
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
    public NotImplementedException() {
    }
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

/**
 * Centralized display strings.
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

    public StringResources() {
    }
}