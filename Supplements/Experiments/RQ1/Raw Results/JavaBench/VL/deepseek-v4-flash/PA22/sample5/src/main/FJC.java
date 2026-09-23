import java.util.*;
import java.util.stream.*;

// ---------- Position ----------
class Position {
    private int x;
    private int y;

    public Position() {}

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

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

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

// ---------- Entity hierarchy ----------
abstract class Entity {
}

class Box extends Entity {
    private int playerId;

    public Box() {}

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}

class Empty extends Entity {
    public Empty() {}
}

class Player extends Entity {
    private int id;

    public Player() {}

    public Player(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}

class Wall extends Entity {
    public Wall() {}
}

// ---------- Action hierarchy ----------
abstract class Action {
    private int initiator;

    public Action() {}

    public Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() { return initiator; }
    public void setInitiator(int initiator) { this.initiator = initiator; }
}

class Exit extends Action {
    public Exit() {}

    public Exit(int initiator) {
        super(initiator);
    }
}

class InvalidInput extends Action {
    private String message;

    public InvalidInput() {}

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

class Undo extends Action {
    public Undo() {}

    public Undo(int initiator) {
        super(initiator);
    }
}

abstract class Move extends Action {
    public Move() {}

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

class Down extends Move {
    public Down() {}

    public Down(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

class Left extends Move {
    public Left() {}

    public Left(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

class Right extends Move {
    public Right() {}

    public Right(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

class Up extends Move {
    public Up() {}

    public Up(int initiator) {
        super(initiator);
    }

    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

// ---------- ActionResult hierarchy ----------
abstract class ActionResult {
    private Action action;

    public ActionResult() {}

    public ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
}

class Success extends ActionResult {
    public Success() {}

    public Success(Action action) {
        super(action);
    }
}

class Failed extends ActionResult {
    private String reason;

    public Failed() {}

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

// ---------- GameMap ----------
class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {}

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
        String[] lines = mapText.split("\n");
        int undoLimit = Integer.parseInt(lines[0].trim());
        int height = lines.length - 1;
        int width = 0;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > width) width = lines[i].length();
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        map.put(pos, new Empty());
                        destinations.add(pos);
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int playerId = c - 'A';
                            playerIds.add(playerId);
                            map.put(pos, new Player(playerId));
                        } else if (c >= 'a' && c <= 'z') {
                            int playerId = c - 'a';
                            boxPlayerIds.add(playerId);
                            map.put(pos, new Box(playerId));
                        } else {
                            // ignore
                        }
                        break;
                }
            }
        }

        // validate
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }
        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) {
                throw new IllegalArgumentException("Box references player ID " + boxPlayerId + " but no such player exists.");
            }
        }

        // check closed boundary (simple check: all border positions must be wall)
        for (int x = 0; x < width; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, height - 1);
            if (!(map.get(top) instanceof Wall) || !(map.get(bottom) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (wall).");
            }
        }
        for (int y = 0; y < height; y++) {
            Position left = new Position(0, y);
            Position right = new Position(width - 1, y);
            if (!(map.get(left) instanceof Wall) || !(map.get(right) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have closed boundary (wall).");
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

    public int getUndoLimit() { return undoLimit; }

    public Optional<Integer> getUndoLimitOptional() {
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

    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }
    public Map<Position, Entity> getMap() { return map; }
    public void setMap(Map<Position, Entity> map) { this.map = map; }
    public void setDestinations(Set<Position> destinations) { this.destinations = destinations; }
    public void setUndoLimit(int undoLimit) { this.undoLimit = undoLimit; }
}

// ---------- GameStateTransition ----------
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
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }

    private GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

// ---------- GameState ----------
class GameState {
    private GameMap gameMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Stack<GameStateTransition> history = new Stack<>();

    public GameState() {}

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        int limit = gameMap.getUndoLimit();
        if (limit == -1) {
            this.undoQuota = -1;
        } else {
            this.undoQuota = limit;
        }
    }

    public Position getPlayerPositionById(int id) {
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = new Position(x, y);
                Entity e = gameMap.getEntity(p);
                if (e instanceof Player && ((Player) e).getId() == id) {
                    return p;
                }
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = new Position(x, y);
                Entity e = gameMap.getEntity(p);
                if (e instanceof Player) {
                    positions.add(p);
                }
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : gameMap.getDestinations()) {
            Entity e = gameMap.getEntity(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = gameMap.getEntity(from);
        if (entity != null) {
            gameMap.putEntity(to, entity);
            gameMap.putEntity(from, new Empty());
        }
    }

    public void checkpoint() {
        // checkpoint is recorded when a box is pushed; we store the current transition
        // but for simplicity we keep history of transitions
    }

    public void undo() {
        if (!history.isEmpty()) {
            GameStateTransition transition = history.pop();
            GameStateTransition reversed = transition.reverse();
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                move(entry.getKey(), entry.getValue());
            }
            if (undoQuota > 0) {
                undoQuota--;
            }
        }
    }

    public void pushTransition(GameStateTransition transition) {
        history.push(transition);
    }

    public int getMapMaxWidth() { return boardWidth; }
    public int getMapMaxHeight() { return boardHeight; }

    public int getBoardWidth() { return boardWidth; }
    public void setBoardWidth(int boardWidth) { this.boardWidth = boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public void setBoardHeight(int boardHeight) { this.boardHeight = boardHeight; }
    public int getUndoQuota() { return undoQuota; }
    public void setUndoQuota(int undoQuota) { this.undoQuota = undoQuota; }
    public GameMap getGameMap() { return gameMap; }
    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
}

// ---------- Interfaces ----------
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

// ---------- AbstractSokobanGame ----------
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified = false;

    public AbstractSokobanGame() {}

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
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
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity nextEntity = state.getEntity(nextPos);
            if (nextEntity instanceof Wall) {
                return new Failed(action, "Cannot move into wall.");
            } else if (nextEntity instanceof Player) {
                return new Failed(action, "Blocked by another player.");
            } else if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindPos);
                if (behindEntity instanceof Empty) {
                    GameStateTransition transition = new GameStateTransition();
                    transition.add(currentPos, nextPos);
                    transition.add(nextPos, behindPos);
                    state.move(currentPos, nextPos);
                    state.move(nextPos, behindPos);
                    state.pushTransition(transition);
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box into obstacle.");
                }
            } else if (nextEntity instanceof Empty) {
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.pushTransition(transition);
                return new Success(action);
            } else {
                return new Failed(action, "Unknown obstacle.");
            }
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action.");
    }

    public boolean isExitSpecified() { return isExitSpecified; }
    public void setExitSpecified(boolean exitSpecified) { isExitSpecified = exitSpecified; }
}

// ---------- TerminalSokobanGame ----------
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {}

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
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (state.isWin()) {
                renderingEngine.render(state);
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }

    public InputEngine getInputEngine() { return inputEngine; }
    public void setInputEngine(InputEngine inputEngine) { this.inputEngine = inputEngine; }
    public RenderingEngine getRenderingEngine() { return renderingEngine; }
    public void setRenderingEngine(RenderingEngine renderingEngine) { this.renderingEngine = renderingEngine; }
}

// ---------- TerminalInputEngine ----------
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {}

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        String input = terminalScanner.nextLine().trim().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        switch (input) {
            case "w": return new Up(0);
            case "a": return new Left(0);
            case "s": return new Down(0);
            case "d": return new Right(0);
            case "r": return new Undo(0);
            case "k": return new Up(1);
            case "h": return new Left(1);
            case "j": return new Down(1);
            case "l": return new Right(1);
            case "u": return new Undo(1);
            default: return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }

    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }
}

// ---------- TerminalRenderingEngine ----------
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {}

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                boolean isDest = state.getDestinations().contains(pos);
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    line.append((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    line.append((char) ('A' + player.getId()));
                } else if (entity instanceof Empty) {
                    if (isDest) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    line.append(' ');
                }
            }
            outputStream.println(line.toString());
        }
    }

    public void message(String content) {
        outputStream.println(content);
    }

    public PrintStream getOutputStream() { return outputStream; }
    public void setOutputStream(PrintStream outputStream) { this.outputStream = outputStream; }
}

// ---------- Reference Implementations ----------

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