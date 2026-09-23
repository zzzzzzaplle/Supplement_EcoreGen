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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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

// ============== Interfaces ==============

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

// ============== Abstract Classes ==============

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public boolean getIsExitSpecified() { return isExitSpecified; }
    public void setIsExitSpecified(boolean isExitSpecified) { this.isExitSpecified = isExitSpecified; }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof Undo) {
            if (state.getUndoQuota() == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            int playerId = action.getInitiator();
            Position playerPos = state.getPlayerPositionById(playerId);
            if (playerPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Move move = (Move) action;
            Position nextPos = move.nextPosition(playerPos);
            Entity entityAtNext = state.getEntity(nextPos);
            if (entityAtNext instanceof Empty) {
                state.move(playerPos, nextPos);
                return new Success(action);
            } else if (entityAtNext instanceof Box) {
                Box box = (Box) entityAtNext;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push box of another player.");
                }
                Position beyond = move.nextPosition(nextPos);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity instanceof Empty) {
                    state.move(playerPos, nextPos);
                    state.move(nextPos, beyond);
                    state.checkpoint();
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box.");
                }
            } else {
                return new Failed(action, "Cannot move.");
            }
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action.");
    }
}

abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() { return initiator; }
    public void setInitiator(int initiator) { this.initiator = initiator; }
}

abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
}

abstract class Entity {
    public Entity() {}
}

// ============== Concrete Classes ==============

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super(new GameState());
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public InputEngine getInputEngine() { return inputEngine; }
    public void setInputEngine(InputEngine inputEngine) { this.inputEngine = inputEngine; }
    public RenderingEngine getRenderingEngine() { return renderingEngine; }
    public void setRenderingEngine(RenderingEngine renderingEngine) { this.renderingEngine = renderingEngine; }

    @Override
    public void run() {
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (!result.getClass().getSimpleName().equals("Success")) {
                renderingEngine.message(((Failed) result).getReason());
            }
            renderingEngine.render(state);
        }
        if (state.isWin()) {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        } else {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this.terminalScanner = new Scanner(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }

    @Override
    public Action fetchAction() {
        String line = terminalScanner.nextLine().trim();
        if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        // Player 0: W, A, S, D, R
        if (line.equalsIgnoreCase("W")) return new Up(0);
        if (line.equalsIgnoreCase("A")) return new Left(0);
        if (line.equalsIgnoreCase("S")) return new Down(0);
        if (line.equalsIgnoreCase("D")) return new Right(0);
        if (line.equalsIgnoreCase("R")) return new Undo(0);
        // Player 1: K, H, J, L, U
        if (line.equalsIgnoreCase("K")) return new Up(1);
        if (line.equalsIgnoreCase("H")) return new Left(1);
        if (line.equalsIgnoreCase("J")) return new Down(1);
        if (line.equalsIgnoreCase("L")) return new Right(1);
        if (line.equalsIgnoreCase("U")) return new Undo(1);
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public PrintStream getOutputStream() { return outputStream; }
    public void setOutputStream(PrintStream outputStream) { this.outputStream = outputStream; }

    @Override
    public void render(GameState state) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position p = Position.of(x, y);
                Entity e = state.getEntity(p);
                char c = '.';
                if (e instanceof Wall) {
                    c = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    c = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    c = (char) ('A' + pl.getId());
                } else {
                    if (state.getDestinations().contains(p)) {
                        c = '@';
                    } else {
                        c = '.';
                    }
                }
                sb.append(c);
            }
            sb.append('\n');
        }
        outputStream.print(sb);
        outputStream.println(String.format(StringResources.UNDO_QUOTA_TEMPLATE,
            state.getUndoQuota() == -1 ? StringResources.UNDO_QUOTA_UNLIMITED : state.getUndoQuota()));
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

class Exit extends Action {
    public Exit() {
        super(-1);
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

class InvalidInput extends Action {
    private String message;

    public InvalidInput() {
        super(-1);
        this.message = "";
    }

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

class Undo extends Action {
    public Undo() {
        super(0);
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

class Down extends Move {
    public Down() {
        super(0);
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
        super(0);
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
        super(0);
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
        super(0);
    }

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x(), currentPosition.y() - 1);
    }
}

class Success extends ActionResult {
    public Success() {
        super(new Exit(-1));
    }

    public Success(Action action) {
        super(action);
    }
}

class Failed extends ActionResult {
    private String reason;

    public Failed() {
        super(new Exit(-1));
        this.reason = "";
    }

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

class Box extends Entity {
    private int playerId;

    public Box() {
        super();
        this.playerId = 0;
    }

    public Box(int playerId) {
        super();
        this.playerId = playerId;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}

class Empty extends Entity {
    public Empty() {
        super();
    }
}

class Player extends Entity {
    private int id;

    public Player() {
        super();
        this.id = 0;
    }

    public Player(int id) {
        super();
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}

class Wall extends Entity {
    public Wall() {
        super();
    }
}

class Position {
    private int x;
    private int y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}

class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {
        this.map = new HashMap<>();
        this.destinations = new HashSet<>();
        this.undoLimit = 0;
        this.maxWidth = 0;
        this.maxHeight = 0;
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

    public Map<Position, Entity> getMap() { return map; }
    public void setMap(Map<Position, Entity> map) { this.map = map; }
    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }
    public Set<Position> getDestinations() { return destinations; }
    public void setDestinations(Set<Position> destinations) { this.destinations = destinations; }
    public int getUndoLimit() { return undoLimit; }
    public void setUndoLimit(int undoLimit) { this.undoLimit = undoLimit; }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\\r?\\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();
        int maxWidth = 0;
        int maxHeight = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            if (line.length() > maxWidth) maxWidth = line.length();
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position p = new Position(x, y - 1);
                if (c == '#') {
                    map.put(p, new Wall());
                } else if (c == '@') {
                    map.put(p, new Empty());
                    destinations.add(p);
                } else if (c == '.') {
                    map.put(p, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    map.put(p, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int id = c - 'a';
                    map.put(p, new Box(id));
                    boxPlayerIds.add(id);
                }
            }
            maxHeight = y;
        }
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }
        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (int id : boxPlayerIds) {
            if (!playerIds.contains(id)) {
                throw new IllegalArgumentException("Box references invalid player ID: " + id);
            }
        }
        // Validate closed boundary
        for (int x = 0; x < maxWidth; x++) {
            if (!(map.get(new Position(x, 0)) instanceof Wall) || !(map.get(new Position(x, maxHeight - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary must be walls.");
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            if (!(map.get(new Position(0, y)) instanceof Wall) || !(map.get(new Position(maxWidth - 1, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary must be walls.");
            }
        }
        GameMap result = new GameMap(map, destinations, undoLimit);
        result.maxWidth = maxWidth;
        result.maxHeight = maxHeight;
        return result;
    }

    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
    }

    public Optional<Integer> getUndoLimit() {
        if (undoLimit == -1) return Optional.empty();
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            }
        }
        return ids;
    }
}

class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Deque<GameStateTransition> checkpointHistory = new ArrayDeque<>();
    private GameStateTransition currentTransition = new GameStateTransition();

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.gameMap = new GameMap();
        this.entities = new HashMap<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        // Initialize entities from map
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = Position.of(x, y);
                entities.put(p, gameMap.getEntity(p));
            }
        }
    }

    public int getBoardWidth() { return boardWidth; }
    public void setBoardWidth(int boardWidth) { this.boardWidth = boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public void setBoardHeight(int boardHeight) { this.boardHeight = boardHeight; }
    public int getUndoQuota() { return undoQuota; }
    public void setUndoQuota(int undoQuota) { this.undoQuota = undoQuota; }
    public GameMap getGameMap() { return gameMap; }
    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
    public Map<Position, Entity> getEntities() { return entities; }
    public void setEntities(Map<Position, Entity> entities) { this.entities = entities; }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return entities.getOrDefault(position, new Empty());
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : gameMap.getDestinations()) {
            if (!(entities.get(dest) instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity e = entities.get(from);
        entities.put(to, e);
        entities.put(from, new Empty());
        currentTransition.add(from, to);
    }

    public void checkpoint() {
        checkpointHistory.push(currentTransition);
        currentTransition = new GameStateTransition();
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            return;
        }
        GameStateTransition last = checkpointHistory.pop();
        // First, apply the reverse of the player move part
        // The last transition recorded player move + box push.
        // We need to reverse both atomically.
        GameStateTransition reversed = last.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity e = entities.get(from);
            entities.put(to, e);
            entities.put(from, new Empty());
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }
}

class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }

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
}