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
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
 * Position on the 2D game grid.
 */
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

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

/**
 * Base class for all entities on the game board.
 */
abstract class Entity {
    public Entity() {
    }
}

/**
 * Empty space.
 */
class Empty extends Entity {
    public Empty() {
        super();
    }
}

/**
 * Wall.
 */
class Wall extends Entity {
    public Wall() {
        super();
    }
}

/**
 * Player.
 */
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

/**
 * Box that can be pushed by a player.
 */
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

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}

/**
 * Abstract base for all game actions.
 */
abstract class Action {
    protected int initiator;

    protected Action() {
        this.initiator = -1;
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

/**
 * Action to exit the game.
 */
class Exit extends Action {
    public Exit() {
        super(-1);
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Action representing an invalid user input.
 */
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * Action to undo the last checkpointed move.
 */
class Undo extends Action {
    public Undo() {
        super(-1);
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract move action.
 */
abstract class Move extends Action {
    protected Move() {
        super();
    }

    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Move up.
 */
class Up extends Move {
    public Up() {
        super();
    }

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Move down.
 */
class Down extends Move {
    public Down() {
        super();
    }

    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Move left.
 */
class Left extends Move {
    public Left() {
        super();
    }

    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Move right.
 */
class Right extends Move {
    public Right() {
        super();
    }

    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Result of executing an action.
 */
abstract class ActionResult {
    protected Action action;

    protected ActionResult() {
        this.action = null;
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

/**
 * Successful action result.
 */
class Success extends ActionResult {
    public Success() {
        super();
    }

    public Success(Action action) {
        super(action);
    }
}

/**
 * Failed action result with a reason.
 */
class Failed extends ActionResult {
    private String reason;

    public Failed() {
        super();
        this.reason = "";
    }

    public Failed(Action action) {
        super(action);
        this.reason = "";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * A transition representing a series of moves (typically a single checkpoint).
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new LinkedHashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new LinkedHashMap<>();
        if (moves != null) {
            this.moves.putAll(moves);
        }
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public Transition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new Transition(moves);
    }

    /**
     * Represents a reversed transition. Encapsulates a position-to-position map.
     */
    public static class Transition {
        private Map<Position, Position> moves;

        public Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        public Map<Position, Position> getMoves() {
            return moves;
        }
    }
}

/**
 * Represents the static map of the game.
 */
class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    public GameMap() {
        this.map = new HashMap<>();
        this.destinations = Collections.emptySet();
        this.undoLimit = -1;
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

    /**
     * Parses a text representation of the map.
     *
     * @param mapText text representation of the map.
     * @return the parsed GameMap.
     */
    public static GameMap parse(String mapText) {
        if (mapText == null || mapText.isEmpty()) {
            throw new IllegalArgumentException("Map text is empty.");
        }
        final String[] lines = mapText.split("\\r?\\n", -1);
        int undoLimit;
        int startLine = 0;
        if (lines.length > 0 && lines[0].matches("-?\\d+")) {
            undoLimit = Integer.parseInt(lines[0].trim());
            startLine = 1;
        } else {
            undoLimit = -1;
        }

        int maxWidth = 0;
        for (int i = startLine; i < lines.length; i++) {
            if (lines[i].length() > maxWidth) {
                maxWidth = lines[i].length();
            }
        }
        int maxHeight = lines.length - startLine;

        Map<Position, Entity> cellMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        List<Integer> boxPlayerIds = new ArrayList<>();

        for (int y = startLine; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position p = new Position(x, y - startLine);
                switch (ch) {
                    case '#':
                        cellMap.put(p, new Wall());
                        break;
                    case '@':
                        destinations.add(p);
                        cellMap.put(p, new Empty());
                        break;
                    case '.':
                        cellMap.put(p, new Empty());
                        break;
                    default:
                        if (ch >= 'A' && ch <= 'Z') {
                            int id = ch - 'A';
                            cellMap.put(p, new Player(id));
                            playerIds.add(id);
                        } else if (ch >= 'a' && ch <= 'z') {
                            int id = ch - 'a';
                            cellMap.put(p, new Box(id));
                            boxPlayerIds.add(id);
                        } else if (ch == ' ' || ch == '\r') {
                            // treat as empty (out of bounds)
                            cellMap.put(p, new Empty());
                        } else {
                            // unknown character, treat as empty
                            cellMap.put(p, new Empty());
                        }
                        break;
                }
            }
            // fill remaining columns with empty (out of bounds)
            for (int x = line.length(); x < maxWidth; x++) {
                Position p = new Position(x, y - startLine);
                cellMap.putIfAbsent(p, new Empty());
            }
        }

        // Validate: closed boundary - ensure all border cells are walls
        for (int x = 0; x < maxWidth; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxHeight - 1);
            Entity topE = cellMap.get(top);
            Entity bottomE = cellMap.get(bottom);
            if (!(topE instanceof Wall) || !(bottomE instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed (not a wall) at top/bottom row.");
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxWidth - 1, y);
            Entity leftE = cellMap.get(left);
            Entity rightE = cellMap.get(right);
            if (!(leftE instanceof Wall) || !(rightE instanceof Wall)) {
                throw new IllegalArgumentException("Map boundary is not closed (not a wall) at left/right column.");
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }

        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }

        for (int boxOwner : boxPlayerIds) {
            if (!playerIds.contains(boxOwner)) {
                throw new IllegalArgumentException("Box references a player ID that does not exist: " + boxOwner);
            }
        }

        GameMap gm = new GameMap(cellMap, destinations, undoLimit);
        return gm;
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        // Workaround because the internal map is unmodifiable in the private constructor case.
        if (this.map instanceof UnmodifiableMapWrapper) {
            ((UnmodifiableMapWrapper) this.map).put(position, entity);
        } else {
            this.map.put(position, entity);
        }
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.ofNullable(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            }
        }
        return Collections.unmodifiableSet(ids);
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

    public int getUndoLimitRaw() {
        return undoLimit;
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

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }

    /**
     * Wrapper for unmodifiable map that allows internal mutation.
     */
    static class UnmodifiableMapWrapper extends HashMap<Position, Entity> {
        public UnmodifiableMapWrapper() {
            super();
        }
    }
}

/**
 * Dynamic game state.
 */
class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> board;
    private Map<Integer, Position> playerPositions;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Deque<GameStateTransition> history;
    private Deque<GameStateTransition> checkpointHistory;
    private GameStateTransition currentTransition;

    public GameState() {
        this.gameMap = new GameMap();
        this.board = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = -1;
        this.history = new ArrayDeque<>();
        this.checkpointHistory = new ArrayDeque<>();
        this.currentTransition = new GameStateTransition();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.board = new HashMap<>();
        Map<Position, Entity> sourceMap = gameMap.getMap();
        if (sourceMap instanceof GameMap.UnmodifiableMapWrapper) {
            for (Map.Entry<Position, Entity> e : sourceMap.entrySet()) {
                this.board.put(e.getKey(), e.getValue());
            }
        } else {
            for (Map.Entry<Position, Entity> e : sourceMap.entrySet()) {
                this.board.put(e.getKey(), e.getValue());
            }
        }
        this.playerPositions = new HashMap<>();
        for (Map.Entry<Position, Entity> e : this.board.entrySet()) {
            if (e.getValue() instanceof Player) {
                this.playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
            }
        }
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitRaw();
        this.history = new ArrayDeque<>();
        this.checkpointHistory = new ArrayDeque<>();
        this.currentTransition = new GameStateTransition();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Position, Entity> getBoard() {
        return board;
    }

    public void setBoard(Map<Position, Entity> board) {
        this.board = board;
    }

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
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

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public Deque<GameStateTransition> getCheckpointHistory() {
        return checkpointHistory;
    }

    public void setCheckpointHistory(Deque<GameStateTransition> checkpointHistory) {
        this.checkpointHistory = checkpointHistory;
    }

    public GameStateTransition getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(GameStateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public Position getPlayerPositionById(int id) {
        return playerPositions.get(id);
    }

    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>(playerPositions.values());
    }

    public Entity getEntity(Position position) {
        return board.get(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : gameMap.getDestinations()) {
            Entity e = board.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = board.get(from);
        board.put(to, moving);
        board.put(from, new Empty());
        if (moving instanceof Player) {
            playerPositions.put(((Player) moving).getId(), to);
        }
    }

    public void checkpoint() {
        if (currentTransition.getMoves() != null && !currentTransition.getMoves().isEmpty()) {
            checkpointHistory.push(currentTransition);
        }
        currentTransition = new GameStateTransition();
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            return;
        }
        GameStateTransition top = checkpointHistory.pop();
        // Undo all moves in the checkpoint's reverse order using its reverse map
        GameStateTransition.Transition reversed = top.reverse();
        Map<Position, Position> moves = reversed.getMoves();
        // Collect keys (new positions) and apply atomically: each move entity at newPos -> originalPos
        // First, we need to gather what needs to go where, but in a single step we can iterate entries.
        // To be atomic, compute a snapshot of board, then apply.
        Map<Position, Entity> snapshot = new HashMap<>(board);
        for (Map.Entry<Position, Position> entry : moves.entrySet()) {
            Position toPos = entry.getKey();
            Position fromPos = entry.getValue();
            Entity e = snapshot.get(toPos);
            snapshot.put(fromPos, e);
            snapshot.put(toPos, new Empty());
        }
        // Update player positions
        for (Map.Entry<Position, Entity> e : snapshot.entrySet()) {
            if (e.getValue() instanceof Player) {
                playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
            }
        }
        board = snapshot;
        // consume undo quota
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public int getMapMaxWidth() {
        return gameMap.getMaxWidth();
    }

    public int getMapMaxHeight() {
        return gameMap.getMaxHeight();
    }
}

/**
 * Interface for a Sokoban game.
 */
interface SokobanGame {
    void run();
}

/**
 * Abstract base implementation of a Sokoban game.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
        this.state = new GameState();
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action);
        }
        if (action instanceof Undo) {
            // Undo only if there is a checkpoint
            if (state.getCheckpointHistory().isEmpty()) {
                return new Failed(action);
            }
            // Check undo quota: 0 not allowed, -1 unlimited
            int quota = state.getUndoQuota();
            if (quota == 0) {
                Failed f = new Failed(action);
                f.setReason(StringResources.UNDO_QUOTA_RUN_OUT);
                return f;
            }
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position current = state.getPlayerPositionById(playerId);
            if (current == null) {
                Failed f = new Failed(action);
                f.setReason(StringResources.PLAYER_NOT_FOUND);
                return f;
            }
            Position next = move.nextPosition(current);

            // Find what is at next
            Entity entityAtNext = state.getEntity(next);

            // Check if next is out of bounds
            if (next.x() < 0 || next.y() < 0 || next.x() >= state.getMapMaxWidth() || next.y() >= state.getMapMaxHeight()) {
                Failed f = new Failed(action);
                f.setReason("Out of bounds");
                return f;
            }

            if (entityAtNext instanceof Wall) {
                Failed f = new Failed(action);
                f.setReason("Blocked by wall");
                return f;
            }
            if (entityAtNext instanceof Player) {
                Failed f = new Failed(action);
                f.setReason("Blocked by another player");
                return f;
            }
            if (entityAtNext instanceof Box) {
                Box box = (Box) entityAtNext;
                if (box.getPlayerId() != playerId) {
                    Failed f = new Failed(action);
                    f.setReason("Cannot push a box that does not belong to you");
                    return f;
                }
                // The space behind the box is at position after next in the same direction
                Position behind = move.nextPosition(next);
                // Check bounds
                if (behind.x() < 0 || behind.y() < 0 || behind.x() >= state.getMapMaxWidth() || behind.y() >= state.getMapMaxHeight()) {
                    Failed f = new Failed(action);
                    f.setReason("Out of bounds");
                    return f;
                }
                Entity behindEntity = state.getEntity(behind);
                if (behindEntity instanceof Wall || behindEntity instanceof Player || behindEntity instanceof Box) {
                    Failed f = new Failed(action);
                    f.setReason("Cannot push box: blocked");
                    return f;
                }
                // Push: move box from next to behind
                state.move(next, behind);
                // Record the move in current transition
                state.getCurrentTransition().add(next, behind);
                // Move player from current to next
                state.move(current, next);
                state.getCurrentTransition().add(current, next);
                // Checkpoint because a box was successfully pushed
                state.checkpoint();
                return new Success(action);
            }
            // Empty space: simply move the player
            state.move(current, next);
            state.getCurrentTransition().add(current, next);
            return new Success(action);
        }
        return new Failed(action);
    }
}

/**
 * Interface for input engines.
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * Interface for rendering engines.
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * Terminal-based input engine.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this.terminalScanner = new Scanner(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String line = terminalScanner.nextLine().trim();
        if (line.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String lower = line.toLowerCase();
        if (lower.equals(StringResources.EXIT_COMMAND_TEXT) || lower.equals("quit")) {
            return new Exit(-1);
        }
        // Single character commands
        if (line.length() == 1) {
            char c = line.charAt(0);
            switch (c) {
                case 'w': return new Up(0);
                case 's': return new Down(0);
                case 'a': return new Left(0);
                case 'd': return new Right(0);
                case 'r': return new Undo(0);
                case 'k': return new Up(1);
                case 'j': return new Down(1);
                case 'h': return new Left(1);
                case 'l': return new Right(1);
                case 'u': return new Undo(1);
                default: break;
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

/**
 * Terminal-based rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        StringBuilder sb = new StringBuilder();
        Set<Position> destinations = state.getDestinations();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                char display;
                if (e instanceof Wall) {
                    display = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    display = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pEntity = (Player) e;
                    display = (char) ('A' + pEntity.getId());
                } else {
                    // Empty
                    if (destinations.contains(p)) {
                        display = '@';
                    } else {
                        display = '.';
                    }
                }
                sb.append(display);
            }
            sb.append('\n');
        }
        outputStream.print(sb.toString());
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

/**
 * Terminal-based Sokoban game.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = new TerminalInputEngine();
        this.renderingEngine = new TerminalRenderingEngine();
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        // Restrict to max two players
        if (gameState != null && gameState.getGameMap() != null) {
            Set<Integer> ids = gameState.getGameMap().getPlayerIds();
            if (ids.size() > 2) {
                throw new IllegalArgumentException("Terminal interface supports at most two players.");
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

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof InvalidInput) {
                renderingEngine.message(((InvalidInput) action).getMessage());
                continue;
            }
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                if (failed.getReason() != null && !failed.getReason().isEmpty()) {
                    renderingEngine.message(failed.getReason());
                } else {
                    renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
                }
                continue;
            }
            renderingEngine.render(state);
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
        if (isExitSpecified()) {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }
}

/**
 * Factory for creating Sokoban games.
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