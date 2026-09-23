import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface for running a Sokoban game.
 */
interface SokobanGame {
    void run();
}

/**
 * Base implementation for Sokoban games.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state == null || this.state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action == null) {
            return new Failed("Null action.");
        }
        if (action instanceof InvalidInput) {
            return new Failed(((InvalidInput) action).getMessage());
        }
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof Undo) {
            if (this.state == null) {
                return new Failed("State not initialized.");
            }
            try {
                this.state.undo();
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage() == null ? "Undo failed." : e.getMessage());
            }
        }
        if (action instanceof Move) {
            if (this.state == null) {
                return new Failed("State not initialized.");
            }
            Move move = (Move) action;
            int initiator = move.getInitiator();
            try {
                Position from = this.state.getPlayerPositionById(initiator);
                Position to = move.nextPosition(from);
                this.state.move(from, to);
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage() == null ? "Move failed." : e.getMessage());
            }
        }
        return new Failed("Unsupported action.");
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return this.state;
    }

    public boolean isExitSpecified() {
        return this.isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        this.isExitSpecified = exitSpecified;
    }

    protected AbstractSokobanGame getThis() {
        return this;
    }

    protected AbstractSokobanGame() {
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

    @Override
    public void run() {
        if (this.renderingEngine == null || this.inputEngine == null || this.state == null) {
            return;
        }
        this.renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            this.renderingEngine.render(this.state);
            this.renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE,
                    this.state.getUndoQuota() < 0 ? StringResources.UNDO_QUOTA_UNLIMITED : this.state.getUndoQuota()));
            Action action = this.inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                this.renderingEngine.message(((Failed) result).getReason());
            }
        }
        this.renderingEngine.render(this.state);
        this.renderingEngine.message(StringResources.WIN_MESSAGE);
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

    public TerminalSokobanGame() {
    }
}

/**
 * Input engine abstraction.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Terminal input implementation.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream, StandardCharsets.UTF_8);
    }

    @Override
    public Action fetchAction() {
        if (this.terminalScanner == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (!this.terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = this.terminalScanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (input.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
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
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }

    public Scanner getTerminalScanner() {
        return this.terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }

    public TerminalInputEngine() {
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
 * Terminal rendering implementation.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        if (this.outputStream == null || state == null) {
            return;
        }
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position p = Position.of(x, y);
                Entity entity = state.getEntity(p);
                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    sb.append((char) ('a' + ((Box) entity).getPlayerId()));
                } else if (entity instanceof Player) {
                    sb.append((char) ('A' + ((Player) entity).getId()));
                } else if (state.getDestinations().contains(p)) {
                    sb.append('@');
                } else {
                    sb.append('.');
                }
            }
            this.outputStream.println(sb.toString());
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

    public TerminalRenderingEngine() {
    }
}

/**
 * Base action class.
 */
abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    protected Action() {
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
    }
}

/**
 * Abstract move action.
 */
abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    protected Move() {
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Down move action.
 */
class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    public Down() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Left move action.
 */
class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    public Left() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Right move action.
 */
class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Right() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Up move action.
 */
class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    public Up() {
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Base action result.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    protected ActionResult() {
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
    }
}

/**
 * Failed action result.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed(String reason) {
        this.reason = reason;
    }

    public Failed() {
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * Base entity type.
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
 * 2D position.
 */
class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
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
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Position(" + x + "," + y + ")";
    }
}

/**
 * Game map, including parsing and validation.
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

    public static GameMap parse(String mapText) {
        if (mapText == null) throw new RuntimeException("Map text is null.");
        String[] lines = mapText.replace("\r\n", "\n").split("\n");
        if (lines.length < 2) throw new RuntimeException("Invalid map.");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new LinkedHashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Character, Integer> playerIds = new HashMap<>();
        int width = -1;
        int height = lines.length - 1;
        int boxCount = 0;
        int destCount = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            if (width < 0) width = line.length();
            if (line.length() != width) throw new RuntimeException("Non-rectangular map.");
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                Position p = Position.of(x, y - 1);
                switch (c) {
                    case '#':
                        map.put(p, new Wall());
                        break;
                    case '.':
                        map.put(p, new Empty());
                        break;
                    case '@':
                        map.put(p, new Empty());
                        destinations.add(p);
                        destCount++;
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int id = c - 'A';
                            playerIds.put(c, id);
                            map.put(p, new Player(id));
                        } else if (Character.isLowerCase(c)) {
                            int id = c - 'a';
                            map.put(p, new Box(id));
                            boxCount++;
                        } else {
                            throw new RuntimeException("Invalid character: " + c);
                        }
                }
            }
        }
        for (int x = 0; x < width; x++) {
            if (!(map.get(Position.of(x, 0)) instanceof Wall) || !(map.get(Position.of(x, height - 1)) instanceof Wall)) {
                throw new RuntimeException("Map is not closed.");
            }
        }
        for (int y = 0; y < height; y++) {
            if (!(map.get(Position.of(0, y)) instanceof Wall) || !(map.get(Position.of(width - 1, y)) instanceof Wall)) {
                throw new RuntimeException("Map is not closed.");
            }
        }
        if (playerIds.isEmpty()) throw new RuntimeException("No player.");
        if (boxCount != destCount) throw new RuntimeException("Box/destination count mismatch.");
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                int id = ((Box) e).getPlayerId();
                if (id < 0 || id > 25) throw new RuntimeException("Invalid box player id.");
                boolean exists = false;
                for (Entity pe : map.values()) {
                    if (pe instanceof Player && ((Player) pe).getId() == id) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) throw new RuntimeException("Box references missing player.");
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        Entity e = this.map.get(position);
        return e == null ? new Empty() : e;
    }

    public void putEntity(Position position, Entity entity) {
        if (this.map instanceof HashMap || this.map instanceof LinkedHashMap) {
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
        Set<Integer> ids = new HashSet<>();
        for (Entity e : this.map.values()) {
            if (e instanceof Player) ids.add(((Player) e).getId());
        }
        return ids;
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
 * Dynamic game state.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Deque<GameStateTransition> history = new ArrayDeque<>();

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
    }

    public GameState() {
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> e : this.gameMap.getMap().entrySet()) {
            if (e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id) {
                return e.getKey();
            }
        }
        throw new RuntimeException(StringResources.PLAYER_NOT_FOUND);
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> set = new HashSet<>();
        for (Map.Entry<Position, Entity> e : this.gameMap.getMap().entrySet()) {
            if (e.getValue() instanceof Player) set.add(e.getKey());
        }
        return set;
    }

    public Entity getEntity(Position position) {
        return this.gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return this.gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position p : this.getDestinations()) {
            if (!(this.getEntity(p) instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = this.getEntity(from);
        if (!(moving instanceof Player)) throw new RuntimeException("No player at source.");
        Entity target = this.getEntity(to);
        if (target instanceof Empty) {
            this.gameMap.putEntity(to, moving);
            this.gameMap.putEntity(from, new Empty());
            this.history.push(new GameStateTransition());
        } else if (target instanceof Box) {
            Box box = (Box) target;
            if (box.getPlayerId() != ((Player) moving).getId()) throw new RuntimeException("Unpushable box.");
            int dx = to.x() - from.x();
            int dy = to.y() - from.y();
            Position beyond = Position.of(to.x() + dx, to.y() + dy);
            if (!(this.getEntity(beyond) instanceof Empty)) throw new RuntimeException("Blocked.");
            GameStateTransition tr = new GameStateTransition();
            tr.add(from, to);
            tr.add(to, beyond);
            this.gameMap.putEntity(beyond, box);
            this.gameMap.putEntity(to, moving);
            this.gameMap.putEntity(from, new Empty());
            this.history.push(tr);
            checkpoint();
        } else {
            throw new RuntimeException("Blocked.");
        }
    }

    public void checkpoint() {
        this.history.push(new GameStateTransition());
    }

    public void undo() {
        if (this.undoQuota == 0) throw new RuntimeException(StringResources.UNDO_QUOTA_RUN_OUT);
        if (this.history.isEmpty()) return;
        GameStateTransition tr = this.history.pop();
        GameStateTransition reverse = tr.reverse();
        for (Map.Entry<Position, Position> e : reverse.getMoves().entrySet()) {
            Entity entity = this.getEntity(e.getKey());
            this.gameMap.putEntity(e.getValue(), entity);
            this.gameMap.putEntity(e.getKey(), new Empty());
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
 * Transition of game state.
 */
class GameStateTransition {
    private Map<Position, Position> moves = new LinkedHashMap<>();

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public Transition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new Transition(moves);
    }

    public Map<Position, Position> getMoves() {
        return this.moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

/**
 * Helper transition type referenced by the snippet.
 */
class Transition extends GameStateTransition {
    public Transition() {
        super();
    }

    public Transition(Map<Position, Position> moves) {
        super();
        this.setMoves(moves);
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