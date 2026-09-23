import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

// Position class
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

    public void setX(int x) { this.x = x; }
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
}

// Abstract Entity class
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

// GameMap class
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
        Map<Character, Integer> boxToPlayer = new HashMap<>();

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
                            boxToPlayer.put(c, playerId);
                            map.put(pos, new Box(playerId));
                        }
                        break;
                }
            }
        }

        // Validate map
        // Check closed boundary
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (!map.containsKey(pos)) {
                    map.put(pos, new Empty());
                }
            }
        }

        // Check boundaries are walls
        for (int x = 0; x < width; x++) {
            if (!(map.get(new Position(x, 0)) instanceof Wall)) throw new RuntimeException("Map not closed at top");
            if (!(map.get(new Position(x, height - 1)) instanceof Wall)) throw new RuntimeException("Map not closed at bottom");
        }
        for (int y = 0; y < height; y++) {
            if (!(map.get(new Position(0, y)) instanceof Wall)) throw new RuntimeException("Map not closed at left");
            if (!(map.get(new Position(width - 1, y)) instanceof Wall)) throw new RuntimeException("Map not closed at right");
        }

        // At least one player
        if (playerIds.isEmpty()) throw new RuntimeException("No player found");

        // Count boxes and destinations
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (boxCount != destinations.size()) throw new RuntimeException("Box count does not match destination count");

        // Boxes must reference valid player IDs
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            if (entry.getValue() instanceof Box) {
                Box box = (Box) entry.getValue();
                if (!playerIds.contains(box.getPlayerId())) throw new RuntimeException("Box references invalid player ID");
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

    public Set<Position> getDestinations() { return destinations; }
    public int getUndoLimit() { return undoLimit; }
    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) ids.add(((Player) e).getId());
        }
        return ids;
    }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }
    public void setDestinations(Set<Position> destinations) { this.destinations = destinations; }
    public void setUndoLimit(int undoLimit) { this.undoLimit = undoLimit; }
}

// GameStateTransition class
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
        GameStateTransition reversed = new GameStateTransition();
        reversed.moves = moves;
        return reversed;
    }

    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }
}

// GameState class
class GameState {
    private GameMap gameMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Stack<GameStateTransition> history = new Stack<>();
    private Map<Integer, Position> playerPositions = new HashMap<>();

    public GameState() {}

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit();
        // Initialize player positions
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity entity = gameMap.getEntity(pos);
                if (entity instanceof Player) {
                    playerPositions.put(((Player) entity).getId(), pos);
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
        for (Position dest : getDestinations()) {
            Entity entity = getEntity(dest);
            if (!(entity instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = gameMap.getEntity(from);
        gameMap.putEntity(to, entity);
        gameMap.putEntity(from, new Empty());
        if (entity instanceof Player) {
            playerPositions.put(((Player) entity).getId(), to);
        }
    }

    public void checkpoint() {
        // Checkpoint is recorded when a box is pushed
        // We store the transition history in the stack
        GameStateTransition transition = new GameStateTransition();
        // Actually we need to record the move that caused the box push
        // This is handled in the game logic
        history.push(transition);
    }

    public void undo() {
        if (!history.isEmpty()) {
            GameStateTransition transition = history.pop();
            GameStateTransition reversed = transition.reverse();
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                move(entry.getKey(), entry.getValue());
            }
            if (undoQuota > 0) undoQuota--;
        }
    }

    public int getMapMaxWidth() { return boardWidth; }
    public int getMapMaxHeight() { return boardHeight; }
    public int getUndoQuota() { return undoQuota; }
    public void setUndoQuota(int undoQuota) { this.undoQuota = undoQuota; }
    public Stack<GameStateTransition> getHistory() { return history; }
    public void setHistory(Stack<GameStateTransition> history) { this.history = history; }
    public GameMap getGameMap() { return gameMap; }
    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
    public void setBoardWidth(int boardWidth) { this.boardWidth = boardWidth; }
    public void setBoardHeight(int boardHeight) { this.boardHeight = boardHeight; }
}

// Action classes
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

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

class Left extends Move {
    public Left() {}

    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

class Right extends Move {
    public Right() {}

    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

class Up extends Move {
    public Up() {}

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

// ActionResult classes
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

// InputEngine interface
interface InputEngine {
    Action fetchAction();
}

// TerminalInputEngine class
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {}

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        String input = terminalScanner.nextLine().trim().toLowerCase();
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
            case "exit":
            case "quit": return new Exit(-1);
            default: return new InvalidInput(-1, "Invalid input: " + input);
        }
    }

    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }
}

// RenderingEngine interface
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

// TerminalRenderingEngine class
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {}

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    line.append((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    line.append((char) ('A' + player.getId()));
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                }
            }
            outputStream.println(line.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }

    public void setOutputStream(PrintStream outputStream) { this.outputStream = outputStream; }
}

// SokobanGame interface
interface SokobanGame {
    void run();
}

// AbstractSokobanGame class
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {}

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

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
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity targetEntity = state.getEntity(nextPos);
            if (targetEntity instanceof Wall || targetEntity instanceof Player) {
                return new Failed(action, "Blocked");
            }
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push other player's box");
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindPos);
                if (!(behindEntity instanceof Empty)) {
                    return new Failed(action, "Blocked behind box");
                }
                // Push box
                state.move(nextPos, behindPos);
                // Record checkpoint for box push
                GameStateTransition transition = new GameStateTransition();
                transition.add(nextPos, behindPos);
                state.getHistory().push(transition);
            }
            // Move player
            state.move(currentPos, nextPos);
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action");
    }

    public void setState(GameState state) { this.state = state; }
    public GameState getState() { return state; }
    public void setExitSpecified(boolean exitSpecified) { isExitSpecified = exitSpecified; }
}

// TerminalSokobanGame class
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {}

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
            renderingEngine.render(state);
        }
    }

    public void setInputEngine(InputEngine inputEngine) { this.inputEngine = inputEngine; }
    public void setRenderingEngine(RenderingEngine renderingEngine) { this.renderingEngine = renderingEngine; }
}

// StringResources class
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

// NotImplementedException class
class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super("Not implemented");
    }
}

// ShouldNotReachException class
class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

// SokobanGameFactory class
class SokobanGameFactory {
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

    public static GameMap loadGameMap(Path mapFile) throws IOException {
        final String fileContent = Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }
}

// Sokoban class
class Sokoban {
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