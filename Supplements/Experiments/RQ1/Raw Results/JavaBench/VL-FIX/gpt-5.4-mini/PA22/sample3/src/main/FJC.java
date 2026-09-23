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
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Marker interface for the Sokoban game runner.
 */
interface SokobanGame {
    void run();
}

/**
 * Base implementation for Sokoban game logic.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame() {
        this(null);
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state == null || this.state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action == null) {
            return new Failed(new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE), StringResources.INVALID_INPUT_MESSAGE);
        }
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            if (this.state == null) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            try {
                this.state.undo();
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
        }
        if (action instanceof Move) {
            if (this.state == null) {
                return new Failed(action, "Game state not initialized.");
            }
            Move move = (Move) action;
            try {
                Position from = this.state.getPlayerPositionById(action.getInitiator());
                Position to = move.nextPosition(from);
                this.state.move(from, to);
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
        }
        return new Failed(action, StringResources.INVALID_INPUT_MESSAGE);
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return this.isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}

/**
 * Terminal-based Sokoban game runner.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public TerminalSokobanGame() {
        super(null);
    }

    @Override
    public void run() {
        if (this.renderingEngine == null || this.inputEngine == null || this.state == null) {
            return;
        }
        this.renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            this.renderingEngine.render(this.state);
            Action action = this.inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                this.renderingEngine.message(((Failed) result).getReason());
            }
            if (this.state.isWin()) {
                this.renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
        if (this.isExitSpecified) {
            this.renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }

    public InputEngine getInputEngine() {
        return this.inputEngine;
    }

    public void setInputEngine(InputEngine inputEngine) {
        this.inputEngine = inputEngine;
    }

    public RenderingEngine getRenderingEngine() {
        return this.renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }
}

/**
 * Input engine abstraction.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal input engine.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public TerminalInputEngine() {
        this(System.in);
    }

    @Override
    public Action fetchAction() {
        if (this.terminalScanner == null || !this.terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String input = this.terminalScanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }

        int initiator = 0;
        if (input.length() == 1) {
            char c = Character.toUpperCase(input.charAt(0));
            switch (c) {
                case 'W':
                    return new Up(0);
                case 'A':
                    return new Left(0);
                case 'S':
                    return new Down(0);
                case 'D':
                    return new Right(0);
                case 'R':
                    return new Undo(0);
                case 'K':
                    return new Up(1);
                case 'H':
                    return new Left(1);
                case 'J':
                    return new Down(1);
                case 'L':
                    return new Right(1);
                case 'U':
                    return new Undo(1);
                default:
                    break;
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }

    public Scanner getTerminalScanner() {
        return this.terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

/**
 * Rendering engine abstraction.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TerminalRenderingEngine() {
        this(System.out);
    }

    @Override
    public void render(GameState state) {
        if (this.outputStream == null || state == null) {
            return;
        }
        int width = Math.max(0, state.getMapMaxWidth());
        int height = Math.max(0, state.getMapMaxHeight());
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                char ch = '.';
                if (entity instanceof Wall) {
                    ch = '#';
                } else if (entity instanceof Player) {
                    int id = ((Player) entity).getId();
                    ch = (char) ('A' + Math.max(0, Math.min(25, id)));
                } else if (entity instanceof Box) {
                    int id = ((Box) entity).getPlayerId();
                    ch = (char) ('a' + Math.max(0, Math.min(25, id)));
                } else if (state.getDestinations().contains(pos)) {
                    ch = '@';
                }
                row.append(ch);
            }
            this.outputStream.println(row);
        }
    }

    @Override
    public void message(String content) {
        if (this.outputStream != null) {
            this.outputStream.println(content);
        }
    }

    public PrintStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

/**
 * Base class for actions.
 */
abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public Action() {
        this(-1);
    }

    public int getInitiator() {
        return this.initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
}

/**
 * Exit action.
 */
class Exit extends Action {
    public Exit(int initiator) {
        super(initiator);
    }

    public Exit() {
        super(-1);
    }
}

/**
 * Invalid input action.
 */
class InvalidInput extends Action {
    private String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public InvalidInput() {
        this(-1, StringResources.INVALID_INPUT_MESSAGE);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * Undo action.
 */
class Undo extends Action {
    public Undo(int initiator) {
        super(initiator);
    }

    public Undo() {
        super(-1);
    }
}

/**
 * Base class for movement actions.
 */
abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public Move() {
        super(-1);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Down movement.
 */
class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    public Down() {
        super(-1);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Left movement.
 */
class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    public Left() {
        super(-1);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Right movement.
 */
class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Right() {
        super(-1);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Up movement.
 */
class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Up() {
        super(-1);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Base class for action results.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public ActionResult() {
        this(null);
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

/**
 * Successful action result.
 */
class Success extends ActionResult {
    public Success(Action action) {
        super(action);
    }

    public Success() {
        super(null);
    }
}

/**
 * Failed action result.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public Failed() {
        this(null, null);
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * Base entity class.
 */
abstract class Entity {
    public Entity() {
    }
}

/**
 * Box entity.
 */
class Box extends Entity {
    private int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public Box() {
        this(0);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}

/**
 * Empty entity.
 */
class Empty extends Entity {
    public Empty() {
    }
}

/**
 * Player entity.
 */
class Player extends Entity {
    private int id;

    public Player(int id) {
        this.id = id;
    }

    public Player() {
        this(0);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

/**
 * Wall entity.
 */
class Wall extends Entity {
    public Wall() {
    }
}

/**
 * Immutable position on the board.
 */
class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        this(0, 0);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}

/**
 * Game map and parsing utilities.
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

    public GameMap() {
        this(0, 0, new HashSet<>(), 0);
    }

    public static GameMap parse(String mapText) {
        if (mapText == null) {
            throw new IllegalArgumentException("Map text is null.");
        }
        String[] lines = mapText.replace("\r", "").split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid map.");
        }
        int undoLimit = Integer.parseInt(lines[0].trim());
        List<String> board = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                board.add(lines[i]);
            }
        }
        Map<Position, Entity> map = new LinkedHashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxIds = new HashSet<>();
        int height = board.size();
        int width = board.stream().mapToInt(String::length).max().orElse(0);
        for (int y = 0; y < height; y++) {
            String row = board.get(y);
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '#';
                Position p = Position.of(x, y);
                if (c == '#') map.put(p, new Wall());
                else if (c == '.') map.put(p, new Empty());
                else if (c == '@') {
                    map.put(p, new Empty());
                    destinations.add(p);
                } else if (Character.isUpperCase(c)) {
                    int id = c - 'A';
                    map.put(p, new Player(id));
                    playerIds.add(id);
                } else if (Character.isLowerCase(c)) {
                    int id = c - 'a';
                    map.put(p, new Box(id));
                    boxIds.add(id);
                } else {
                    map.put(p, new Empty());
                }
            }
        }
        if (playerIds.isEmpty()) throw new IllegalArgumentException("There must be at least one player.");
        if (destinations.size() != boxIds.size()) throw new IllegalArgumentException("Destination count must equal box count.");
        if (!playerIds.containsAll(boxIds)) throw new IllegalArgumentException("Boxes must reference valid players.");
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return this.map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (this.map instanceof java.util.HashMap) {
            this.map.put(position, entity);
        } else {
            throw new UnsupportedOperationException("Map is immutable.");
        }
    }

    public Set<Position> getDestinations() {
        return this.destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(this.undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return this.map.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(e -> ((Player) e.getValue()).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public Map<Position, Entity> getMap() {
        return this.map;
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
 * Game state with move and undo support.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Deque<GameStateTransition> history;

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap == null ? 0 : gameMap.getMaxWidth();
        this.boardHeight = gameMap == null ? 0 : gameMap.getMaxHeight();
        this.undoQuota = gameMap == null ? 0 : gameMap.getUndoLimit().orElse(0);
        this.history = new ArrayDeque<>();
    }

    public GameState() {
        this(null);
    }

    public Position getPlayerPositionById(int id) {
        if (this.gameMap == null) throw new IllegalStateException(StringResources.PLAYER_NOT_FOUND);
        for (Map.Entry<Position, Entity> e : this.gameMap.getMap().entrySet()) {
            if (e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id) return e.getKey();
        }
        throw new IllegalStateException(StringResources.PLAYER_NOT_FOUND);
    }

    public Set<Position> getAllPlayerPositions() {
        if (this.gameMap == null) return Collections.emptySet();
        return this.gameMap.getMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Entity getEntity(Position position) {
        if (this.gameMap == null) return null;
        return this.gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return this.gameMap == null ? Collections.emptySet() : this.gameMap.getDestinations();
    }

    public boolean isWin() {
        if (this.gameMap == null) return false;
        for (Position p : this.getDestinations()) {
            Entity e = this.getEntity(p);
            if (!(e instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        if (this.gameMap == null) return;
        Entity mover = this.gameMap.getEntity(from);
        if (!(mover instanceof Player)) throw new IllegalStateException("No player at position.");
        Entity target = this.gameMap.getEntity(to);
        GameStateTransition transition = new GameStateTransition();
        if (target == null || target instanceof Empty) {
            this.gameMap.putEntity(to, mover);
            this.gameMap.putEntity(from, new Empty());
            transition.add(from, to);
        } else if (target instanceof Box) {
            int playerId = ((Player) mover).getId();
            if (((Box) target).getPlayerId() != playerId) throw new IllegalStateException("Unpushable box.");
            int dx = to.x() - from.x();
            int dy = to.y() - from.y();
            Position behind = Position.of(to.x() + dx, to.y() + dy);
            Entity behindEntity = this.gameMap.getEntity(behind);
            if (behindEntity != null && !(behindEntity instanceof Empty)) throw new IllegalStateException("Blocked.");
            this.gameMap.putEntity(behind, target);
            this.gameMap.putEntity(to, mover);
            this.gameMap.putEntity(from, new Empty());
            transition.add(to, behind);
            transition.add(from, to);
            checkpoint();
        } else {
            throw new IllegalStateException("Blocked.");
        }
        this.history.push(transition);
    }

    public void checkpoint() {
        if (this.history == null) this.history = new ArrayDeque<>();
    }

    public void undo() {
        if (this.undoQuota == 0) throw new IllegalStateException(StringResources.UNDO_QUOTA_RUN_OUT);
        if (this.history == null || this.history.isEmpty()) throw new IllegalStateException(StringResources.UNDO_QUOTA_RUN_OUT);
        GameStateTransition transition = this.history.pop().reverse();
        for (Map.Entry<Position, Position> entry : transition.getMoves().entrySet()) {
            Entity entity = this.gameMap.getEntity(entry.getKey());
            this.gameMap.putEntity(entry.getValue(), entity);
            this.gameMap.putEntity(entry.getKey(), new Empty());
        }
        if (this.undoQuota > 0) this.undoQuota--;
    }

    public int getMapMaxWidth() {
        return this.boardWidth;
    }

    public int getMapMaxHeight() {
        return this.boardHeight;
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getBoardHeight() {
        return this.boardHeight;
    }

    public void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    public int getUndoQuota() {
        return this.undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Deque<GameStateTransition> getHistory() {
        return this.history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }
}

/**
 * Game state transition mapping.
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new LinkedHashMap<>();
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
        this.moves = new LinkedHashMap<>(moves);
    }

    public Map<Position, Position> getMoves() {
        return this.moves;
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
 * Shared string resources.
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