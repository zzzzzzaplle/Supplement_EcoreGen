import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * NotImplementedException
 */
class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
    }
}

/**
 * ShouldNotReachException
 */
class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

/**
 * StringResources
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

/**
 * Position
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

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

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
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

/**
 * Entity (abstract)
 */
abstract class Entity {
    public Entity() {
    }
}

/**
 * Empty
 */
class Empty extends Entity {
    public Empty() {
    }
}

/**
 * Wall
 */
class Wall extends Entity {
    public Wall() {
    }
}

/**
 * Box
 */
class Box extends Entity {
    private int playerId;

    public Box() {
        this.playerId = 0;
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

/**
 * Player
 */
class Player extends Entity {
    private int id;

    public Player() {
        this.id = 0;
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

/**
 * GameMap
 */
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

    public Map<Position, Entity> getMap() {
        return map;
    }

    public void setMap(Map<Position, Entity> map) {
        this.map = map;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public static GameMap parse(String mapText) {
        if (mapText == null || mapText.isEmpty()) {
            throw new IllegalArgumentException("Map text is empty.");
        }
        String[] lines = mapText.split("\\r?\\n");
        int undoLimit = 0;
        int startLine = 0;
        if (lines.length > 0 && lines[0].matches("-?\\d+")) {
            undoLimit = Integer.parseInt(lines[0].trim());
            startLine = 1;
        }
        Map<Position, Entity> entityMap = new LinkedHashMap<>();
        Set<Position> destinations = new HashSet<>();
        int maxX = 0;
        int maxY = 0;
        Set<Integer> playerIds = new HashSet<>();
        int boxCount = 0;
        for (int y = startLine; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - startLine);
                if (c == '#') {
                    entityMap.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    entityMap.put(pos, new Empty());
                } else if (c == '.') {
                    entityMap.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int id = c - 'A';
                    entityMap.put(pos, new Player(id));
                    playerIds.add(id);
                } else if (c >= 'a' && c <= 'z') {
                    int id = c - 'a';
                    entityMap.put(pos, new Box(id));
                    boxCount++;
                } else if (c == ' ') {
                    // ignore
                } else {
                    entityMap.put(pos, new Empty());
                }
                if (x > maxX) maxX = x;
            }
            if ((y - startLine) > maxY) maxY = y - startLine;
        }
        // validate
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player.");
        }
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes.");
        }
        // validate boxes have valid player ids
        for (Map.Entry<Position, Entity> e : entityMap.entrySet()) {
            if (e.getValue() instanceof Box) {
                Box b = (Box) e.getValue();
                if (!playerIds.contains(b.getPlayerId())) {
                    throw new IllegalArgumentException("Box references invalid player id: " + b.getPlayerId());
                }
            }
        }
        // validate closed boundary
        if (!isClosedBoundary(entityMap, maxX, maxY)) {
            throw new IllegalArgumentException("Map must be a closed boundary.");
        }
        GameMap gm = new GameMap(entityMap, destinations, undoLimit);
        return gm;
    }

    private static boolean isClosedBoundary(Map<Position, Entity> map, int maxX, int maxY) {
        for (int x = 0; x <= maxX; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, maxY);
            Entity t = map.get(top);
            Entity b = map.get(bottom);
            if (!(t instanceof Wall) || !(b instanceof Wall)) return false;
        }
        for (int y = 0; y <= maxY; y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxX, y);
            Entity l = map.get(left);
            Entity r = map.get(right);
            if (!(l instanceof Wall) || !(r instanceof Wall)) return false;
        }
        // also check that there are no gaps in the wall boundary at corners
        return true;
    }

    public Entity getEntity(Position position) {
        if (position == null) return null;
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        if (position == null) return;
        if (map instanceof HashMap) {
            map.put(position, entity);
        } else {
            // for unmodifiable map used in parsed version, we need a mutable copy
            // Since parsed map is unmodifiable, callers should use GameState to mutate
            // but for compliance, allow it via wrapping
            Map<Position, Entity> mutable = new HashMap<>(map);
            mutable.put(position, entity);
            this.map = mutable;
        }
    }

    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            } else if (e instanceof Box) {
                ids.add(((Box) e).getPlayerId());
            }
        }
        return ids;
    }
}

/**
 * GameStateTransition
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
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

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        final Map<Position, Position> reversed = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(reversed);
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }
}

/**
 * GameState
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private int undoLimit;

    public GameState() {
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>();
        this.history = new ArrayDeque<>();
        this.undoQuota = -1;
        this.undoLimit = 0;
    }

    public GameState(GameMap gameMap) {
        this();
        if (gameMap == null) return;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.undoLimit = gameMap.getUndoLimit();
        this.undoQuota = this.undoLimit;
        // copy entities
        Map<Position, Entity> raw = gameMap.getMap();
        Map<Position, Entity> mutable = new HashMap<>(raw);
        this.entities = mutable;
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

    public int getUndoLimit() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
    }

    public Set<Position> getDestinationsSet() {
        return destinations;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> e : entities.entrySet()) {
            if (e.getValue() instanceof Player) {
                if (((Player) e.getValue()).getId() == id) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> e : entities.entrySet()) {
            if (e.getValue() instanceof Player) {
                positions.add(e.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        if (position == null) return null;
        return entities.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position d : destinations) {
            Entity e = entities.get(d);
            if (!(e instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity e = entities.get(from);
        entities.remove(from);
        entities.put(to, e);
    }

    public void checkpoint() {
        // snapshot current entity map for undo
        Map<Position, Entity> snapshot = new HashMap<>(entities);
        // no-op kept for API compatibility; actual transitions stored on move
    }

    public void undo() {
        if (history.isEmpty()) return;
        if (undoQuota == 0) return;
        // find the most recent checkpoint (transition)
        GameStateTransition transition = history.pop();
        if (transition == null) return;
        Map<Position, Position> moves = transition.getMoves();
        for (Map.Entry<Position, Position> entry : moves.entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity e = entities.get(to);
            entities.remove(to);
            entities.put(from, e);
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

    /**
     * Apply a move with given action initiator. Returns the transition if a box was pushed, else null.
     * This is used by the game loop to record history.
     */
    public GameStateTransition applyMove(int initiatorId, Position next) {
        Position current = getPlayerPositionById(initiatorId);
        if (current == null) return null;
        GameStateTransition transition = new GameStateTransition();
        Entity target = entities.get(next);
        if (target instanceof Box) {
            Box box = (Box) target;
            if (box.getPlayerId() != initiatorId) return null;
            int dx = Integer.compare(next.x(), current.x());
            int dy = Integer.compare(next.y(), current.y());
            Position beyond = new Position(next.x() + dx, next.y() + dy);
            Entity beyondEntity = entities.get(beyond);
            if (beyondEntity instanceof Wall) return null;
            if (beyondEntity instanceof Player) return null;
            if (beyondEntity instanceof Box) return null;
            // move box
            entities.put(beyond, box);
            entities.remove(next);
            transition.add(next, beyond);
        } else if (target instanceof Wall) {
            return null;
        } else if (target instanceof Player) {
            return null;
        }
        // move player
        entities.remove(current);
        Player player = null;
        for (Entity e : entities.values()) {
            if (e instanceof Player && ((Player) e).getId() == initiatorId) {
                player = (Player) e;
                break;
            }
        }
        if (player == null) {
            // player entity was removed already; need to re-add
            player = new Player(initiatorId);
        }
        entities.put(next, player);
        transition.add(current, next);
        return transition;
    }
}

/**
 * Action (abstract)
 */
abstract class Action {
    protected int initiator;

    public Action() {
        this.initiator = 0;
    }

    public Action(int initiator) {
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
 * Exit
 */
class Exit extends Action {
    public Exit() {
        super();
    }

    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * InvalidInput
 */
class InvalidInput extends Action {
    private String message;

    public InvalidInput() {
        super();
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
 * Undo
 */
class Undo extends Action {
    public Undo() {
        super();
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Move (abstract)
 */
abstract class Move extends Action {
    public Move() {
        super();
    }

    public Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Up
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
 * Down
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
 * Left
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
 * Right
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
 * ActionResult (abstract)
 */
abstract class ActionResult {
    protected Action action;

    public ActionResult() {
        this.action = null;
    }

    public ActionResult(Action action) {
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
 * Success
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
 * Failed
 */
class Failed extends ActionResult {
    private String reason;

    public Failed() {
        super();
        this.reason = "";
    }

    public Failed(String reason) {
        super(null);
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
 * InputEngine (interface)
 */
interface InputEngine {
    Action fetchAction();
}

/**
 * TerminalInputEngine
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
        if (line.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || line.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (line.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        // Single character commands
        if (line.length() == 1) {
            char c = Character.toLowerCase(line.charAt(0));
            switch (c) {
                case 'w': return new Up(0);
                case 'a': return new Left(0);
                case 's': return new Down(0);
                case 'd': return new Right(0);
                case 'r': return new Undo(0);
                case 'k': return new Up(1);
                case 'h': return new Left(1);
                case 'j': return new Down(1);
                case 'l': return new Right(1);
                case 'u': return new Undo(1);
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

/**
 * RenderingEngine (interface)
 */
interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

/**
 * TerminalRenderingEngine
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
        if (state == null) return;
        int w = state.getMapMaxWidth();
        int h = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        // build base map representation
        char[][] grid = new char[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                grid[y][x] = ' ';
            }
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                if (e == null) {
                    if (destinations.contains(p)) {
                        grid[y][x] = '@';
                    } else {
                        grid[y][x] = '.';
                    }
                } else if (e instanceof Wall) {
                    grid[y][x] = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    grid[y][x] = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    grid[y][x] = (char) ('A' + pl.getId());
                } else if (e instanceof Empty) {
                    if (destinations.contains(p)) {
                        grid[y][x] = '@';
                    } else {
                        grid[y][x] = '.';
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                sb.append(grid[y][x]);
            }
            sb.append('\n');
        }
        outputStream.print(sb.toString());
    }

    @Override
    public void message(String content) {
        if (content == null) content = "";
        outputStream.println(content);
    }
}

/**
 * SokobanGame (interface)
 */
interface SokobanGame {
    void run();
}

/**
 * AbstractSokobanGame
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
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
            InvalidInput ii = (InvalidInput) action;
            return new Failed(ii.getMessage() != null ? ii.getMessage() : StringResources.INVALID_INPUT_MESSAGE);
        }
        if (action instanceof Undo) {
            if (state.getUndoQuota() == 0) {
                return new Failed(StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = action.getInitiator();
            Position current = state.getPlayerPositionById(initiator);
            if (current == null) {
                return new Failed(StringResources.PLAYER_NOT_FOUND);
            }
            Position next = move.nextPosition(current);
            Entity target = state.getEntity(next);
            if (target instanceof Wall) {
                return new Failed("Blocked by wall.");
            }
            if (target instanceof Player) {
                return new Failed("Blocked by another player.");
            }
            if (target instanceof Box) {
                Box box = (Box) target;
                if (box.getPlayerId() != initiator) {
                    return new Failed("Cannot push other player's box.");
                }
                int dx = Integer.compare(next.x(), current.x());
                int dy = Integer.compare(next.y(), current.y());
                Position beyond = new Position(next.x() + dx, next.y() + dy);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity instanceof Wall || beyondEntity instanceof Box || beyondEntity instanceof Player) {
                    return new Failed("Box cannot be pushed.");
                }
                // apply move
                GameStateTransition transition = state.applyMove(initiator, next);
                if (transition == null) {
                    return new Failed("Move failed.");
                }
                // record transition in history
                state.getHistory().push(transition);
                return new Success(action);
            }
            // empty space
            GameStateTransition transition = state.applyMove(initiator, next);
            if (transition == null) {
                return new Failed("Move failed.");
            }
            state.getHistory().push(transition);
            return new Success(action);
        }
        return new Failed("Unknown action.");
    }
}

/**
 * TerminalSokobanGame
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = new TerminalInputEngine();
        this.renderingEngine = new TerminalRenderingEngine();
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
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
        renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE,
                state.getUndoQuota() == -1 ? StringResources.UNDO_QUOTA_UNLIMITED : String.valueOf(state.getUndoQuota())));
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                setExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE,
                    state.getUndoQuota() == -1 ? StringResources.UNDO_QUOTA_UNLIMITED : String.valueOf(state.getUndoQuota())));
            renderingEngine.render(state);
        }
    }
}

/**
 * SokobanGameFactory
 */
class SokobanGameFactory {

    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            final java.net.URL resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                file = Path.of(resource.toURI());
            } catch (java.net.URISyntaxException e) {
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

/**
 * Sokoban
 */
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