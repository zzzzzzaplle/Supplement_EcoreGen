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
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

interface SokobanGame {
    void run();
}

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || (this.state != null && this.state.isWin());
    }

    protected ActionResult processAction(Action action) {
        if (action == null) {
            return new Failed("Null action.");
        }
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            if (this.state == null) {
                return new Failed("No game state.");
            }
            return this.state.undo() ? new Success(action) : new Failed(StringResources.UNDO_QUOTA_RUN_OUT);
        }
        if (action instanceof Move) {
            if (this.state == null) {
                return new Failed("No game state.");
            }
            Move move = (Move) action;
            Integer initiator = action.getInitiator();
            Position current = this.state.getPlayerPositionById(initiator);
            if (current == null) {
                return new Failed(StringResources.PLAYER_NOT_FOUND);
            }
            Position next = move.nextPosition(current);
            Entity target = this.state.getEntity(next);
            if (target instanceof Empty) {
                this.state.checkpoint();
                this.state.move(current, next);
                return new Success(action);
            }
            if (target instanceof Box) {
                Box box = (Box) target;
                if (box.getPlayerId() != initiator) {
                    return new Failed("Box cannot be pushed by this player.");
                }
                Position beyond = move.nextPosition(next);
                Entity behind = this.state.getEntity(beyond);
                if (behind instanceof Empty) {
                    this.state.checkpoint();
                    this.state.move(next, beyond);
                    this.state.move(current, next);
                    return new Success(action);
                }
                return new Failed("Movement blocked.");
            }
            return new Failed("Movement blocked.");
        }
        return new Failed("Unsupported action.");
    }

    protected void setExitSpecified(boolean exitSpecified) {
        this.isExitSpecified = exitSpecified;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecifiedPublic(boolean exitSpecified) {
        this.isExitSpecified = exitSpecified;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}

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

    public void run() {
        if (this.renderingEngine != null) {
            this.renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        }
        while (!shouldStop()) {
            if (this.renderingEngine != null && this.state != null) {
                this.renderingEngine.render(this.state);
            }
            Action action = this.inputEngine != null ? this.inputEngine.fetchAction() : new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            ActionResult result = processAction(action);
            if (result instanceof Failed && this.renderingEngine != null) {
                this.renderingEngine.message(((Failed) result).getReason());
            }
        }
        if (this.state != null && this.state.isWin() && this.renderingEngine != null) {
            this.renderingEngine.message(StringResources.WIN_MESSAGE);
        }
        if (this.isExitSpecified() && this.renderingEngine != null) {
            this.renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
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

interface InputEngine {
    Action fetchAction();
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        if (this.terminalScanner == null || !this.terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String input = this.terminalScanner.nextLine().trim();
        if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT)) {
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

interface RenderingEngine {
    void render(GameState state);

    void message(String content);
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

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
                Entity e = state.getEntity(p);
                if (e instanceof Wall) {
                    sb.append('#');
                } else if (e instanceof Box) {
                    sb.append((char) ('a' + ((Box) e).getPlayerId()));
                } else if (e instanceof Player) {
                    sb.append((char) ('A' + ((Player) e).getId()));
                } else if (state.getDestinations().contains(p)) {
                    sb.append('@');
                } else {
                    sb.append('.');
                }
            }
            this.outputStream.println(sb.toString());
        }
    }

    public void message(String content) {
        if (this.outputStream != null) {
            this.outputStream.println(content);
        }
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

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

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

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

abstract class Entity {
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    public int hashCode() {
        return 31 * x + y;
    }

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
        String[] lines = mapText == null ? new String[0] : mapText.split("\\R");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Empty map.");
        }
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new LinkedHashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int height = lines.length - 1;
        int width = 0;
        int boxCount = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            width = Math.max(width, line.length());
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position p = Position.of(x, y - 1);
                switch (c) {
                    case '#':
                        map.put(p, new Wall());
                        break;
                    case '@':
                        destinations.add(p);
                        map.put(p, new Empty());
                        break;
                    case '.':
                        map.put(p, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int id = c - 'A';
                            playerIds.add(id);
                            map.put(p, new Player(id));
                        } else if (Character.isLowerCase(c)) {
                            int id = c - 'a';
                            boxCount++;
                            map.put(p, new Box(id));
                        } else {
                            map.put(p, new Empty());
                        }
                        break;
                }
            }
        }
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Destination and box count mismatch.");
        }
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                if (!playerIds.contains(((Box) e).getPlayerId())) {
                    throw new IllegalArgumentException("Invalid box player reference.");
                }
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        Entity e = this.map.get(position);
        return e == null ? new Empty() : e;
    }

    public void putEntity(Position position, Entity entity) {
        if (this.map instanceof java.util.Collections.UnmodifiableMap) {
            throw new UnsupportedOperationException("Immutable map.");
        }
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return this.map.values().stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .map(Player::getId)
                .collect(Collectors.toSet());
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
    private GameMap gameMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> history;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = Position.of(x, y);
                Entity e = gameMap.getEntity(p);
                if (e instanceof Player) {
                    playerPositions.put(((Player) e).getId(), p);
                }
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
        Entity e = gameMap.getEntity(from);
        if (e instanceof Player) {
            playerPositions.put(((Player) e).getId(), to);
        }
        if (gameMap.getMap() != null) {
            gameMap.getMap().put(to, e);
            gameMap.getMap().put(from, new Empty());
        }
    }

    public void checkpoint() {
        if (undoQuota == 0) {
            return;
        }
        history.push(new GameStateTransition());
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        GameStateTransition transition = history.pop();
        GameStateTransition reversed = transition.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            move(entry.getKey(), entry.getValue());
        }
        return true;
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
}

class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
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
        this.moves = new HashMap<>(moves);
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

class SokobanGameFactory {

    /**
     * Factory for creating Sokoban games
     */

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

class Sokoban {

    /**
     * The holder of the entry point of the game.
     */

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

class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }
}

class ShouldNotReachException extends RuntimeException {

    /**
     * Thrown when a branch should not be reached. Used to avoid compilation error.
     */

    /**
     * Create a new should not reach exception.
     */
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

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