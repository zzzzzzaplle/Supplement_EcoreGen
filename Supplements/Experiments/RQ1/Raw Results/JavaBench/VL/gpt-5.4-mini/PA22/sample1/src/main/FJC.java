import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Marker interface for a Sokoban game.
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

    protected AbstractSokobanGame() {
    }

    protected boolean shouldStop() {
        return isExitSpecified || (state != null && state.isWin());
    }

    protected ActionResult processAction(Action action) {
        if (action == null) {
            return new Failed(new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE), StringResources.INVALID_INPUT_MESSAGE);
        }
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            if (state == null) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            try {
                state.undo();
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
        }
        if (action instanceof Move) {
            if (state == null) {
                return new Failed(action, "No game state.");
            }
            Move move = (Move) action;
            try {
                Position current = state.getPlayerPositionById(action.getInitiator());
                if (current == null) {
                    return new Failed(action, StringResources.PLAYER_NOT_FOUND);
                }
                Position next = move.nextPosition(current);
                Entity entity = state.getEntity(next);
                if (entity instanceof Empty) {
                    state.move(current, next);
                    return new Success(action);
                }
                if (entity instanceof Box) {
                    Box box = (Box) entity;
                    if (box.getPlayerId() != action.getInitiator()) {
                        return new Failed(action, "Unpushable box.");
                    }
                    Position beyond = move.nextPosition(next);
                    Entity beyondEntity = state.getEntity(beyond);
                    if (!(beyondEntity instanceof Empty)) {
                        return new Failed(action, "Blocked.");
                    }
                    state.checkpoint();
                    state.move(next, beyond);
                    state.move(current, next);
                    return new Success(action);
                }
                return new Failed(action, "Blocked.");
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
        }
        return new Failed(action, "Unsupported action.");
    }

    protected boolean isExitSpecified() {
        return isExitSpecified;
    }

    protected void setExitSpecified(boolean exitSpecified) {
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
 * Terminal Sokoban game implementation.
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
        super();
    }

    @Override
    public void run() {
        if (renderingEngine == null || inputEngine == null || state == null) {
            return;
        }
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            renderingEngine.render(state);
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (isExitSpecified()) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
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
    }

    @Override
    public Action fetchAction() {
        if (terminalScanner == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = terminalScanner.nextLine().trim();
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
        return terminalScanner;
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
    }

    @Override
    public void render(GameState state) {
        if (state == null || outputStream == null) {
            return;
        }
        int maxY = state.getMapMaxHeight();
        int maxX = state.getMapMaxWidth();
        for (int y = 0; y < maxY; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < maxX; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                char ch = '.';
                if (entity instanceof Wall) {
                    ch = '#';
                } else if (entity instanceof Box) {
                    ch = (char) ('a' + ((Box) entity).getPlayerId() % 26);
                } else if (entity instanceof Player) {
                    ch = (char) ('A' + ((Player) entity).getId() % 26);
                } else if (state.getDestinations().contains(pos)) {
                    ch = '@';
                }
                sb.append(ch);
            }
            outputStream.println(sb.toString());
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
 * Base action type.
 */
abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    protected Action() {
    }

    public int getInitiator() {
        return initiator;
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
        return message;
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
 * Base move action.
 */
abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    protected Move() {
    }

    public abstract Position nextPosition(Position currentPosition);
}

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
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

class Success extends ActionResult {
    public Success(Action action) {
        super(action);
    }

    public Success() {
    }
}

class Failed extends ActionResult {
    private String reason;

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public Failed() {
    }

    public String getReason() {
        return reason;
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

class Box extends Entity {
    private int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public Box() {
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

    public Player(int id) {
        this.id = id;
    }

    public Player() {
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

/**
 * Immutable 2D position.
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
        return Objects.hash(x, y);
    }
}

/**
 * Game map definition and parsing logic.
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
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.replace("\r\n", "\n").split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        int height = lines.length - 1;
        int width = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            width = Math.max(width, line.length());
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position p = Position.of(x, y - 1);
                if (c == '#') map.put(p, new Wall());
                else if (c == '@') destinations.add(p);
                else if (c == '.') map.put(p, new Empty());
                else if (Character.isUpperCase(c)) map.put(p, new Player(c - 'A'));
                else if (Character.isLowerCase(c)) map.put(p, new Box(c - 'a'));
                else map.put(p, new Empty());
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        Entity entity = map.get(position);
        if (entity != null) {
            return entity;
        }
        if (destinations != null && destinations.contains(position)) {
            return new Empty();
        }
        return new Wall();
    }

    public void putEntity(Position position, Entity entity) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.ofNullable(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        if (map != null) {
            for (Entity e : map.values()) {
                if (e instanceof Player) {
                    ids.add(((Player) e).getId());
                }
                if (e instanceof Box) {
                    ids.add(((Box) e).getPlayerId());
                }
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
        this.boardWidth = gameMap == null ? 0 : gameMap.getMaxWidth();
        this.boardHeight = gameMap == null ? 0 : gameMap.getMaxHeight();
        this.undoQuota = gameMap == null ? 0 : gameMap.getUndoLimit().orElse(0);
    }

    public GameState() {
    }

    public Position getPlayerPositionById(int id) {
        if (gameMap == null || gameMap.getMap() == null) return null;
        return gameMap.getMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    public Set<Position> getAllPlayerPositions() {
        if (gameMap == null || gameMap.getMap() == null) return new HashSet<>();
        return gameMap.getMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Entity getEntity(Position position) {
        return gameMap == null ? new Empty() : gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return gameMap == null ? new HashSet<>() : gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position p : getDestinations()) {
            if (!(getEntity(p) instanceof Box)) {
                return false;
            }
        }
        return !getDestinations().isEmpty();
    }

    public void move(Position from, Position to) {
        if (gameMap == null || gameMap.getMap() == null) return;
        Entity entity = gameMap.getMap().remove(from);
        if (entity != null) {
            gameMap.getMap().put(to, entity);
        }
    }

    public void checkpoint() {
        history.push(new GameStateTransition());
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public void undo() {
        if (history.isEmpty()) {
            throw new RuntimeException(StringResources.UNDO_QUOTA_RUN_OUT);
        }
        history.pop();
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

/**
 * Transition between two game states.
 */
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

    private GameStateTransition(Map<Position, Position> moves) {
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

    public SokobanGameFactory() {
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

    public Sokoban() {
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
 * Centralized string resources.
 */
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