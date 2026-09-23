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
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
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
 * Position class for 2D grid coordinates.
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
        return this.x;
    }

    public int y() {
        return this.y;
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
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

abstract class Entity {
    public Entity() {
    }
}

class Box extends Entity {
    private int playerId;

    public Box() {
        this.playerId = 0;
    }

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
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
        this.id = 0;
    }

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

class Wall extends Entity {
    public Wall() {
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
        this.maxWidth = 0;
        this.maxHeight = 0;
        this.destinations = new HashSet<>();
        this.undoLimit = 0;
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
        if (mapText == null) {
            throw new IllegalArgumentException("Map text is null");
        }
        String[] lines = mapText.split("\\r?\\n");
        int undoLimit = 0;
        int startLine = 0;
        if (lines.length > 0 && lines[0].matches("-?\\d+")) {
            undoLimit = Integer.parseInt(lines[0].trim());
            startLine = 1;
        }

        List<String> mapLines = new ArrayList<>();
        for (int i = startLine; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                mapLines.add(lines[i]);
            }
        }

        if (mapLines.isEmpty()) {
            throw new IllegalArgumentException("Map is empty");
        }

        int maxWidth = 0;
        for (String line : mapLines) {
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> playerPositions = new HashMap<>();
        List<Position> boxPositions = new ArrayList<>();

        for (int y = 0; y < mapLines.size(); y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                if (c == '#') {
                    map.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    map.put(pos, new Empty());
                } else if (c == '.') {
                    map.put(pos, new Empty());
                } else if (c >= 'A' && c <= 'Z') {
                    int playerId = c - 'A';
                    map.put(pos, new Player(playerId));
                    playerPositions.put(playerId, pos);
                } else if (c >= 'a' && c <= 'z') {
                    int playerId = c - 'a';
                    map.put(pos, new Box(playerId));
                    boxPositions.add(pos);
                } else if (c == ' ') {
                    // ignore spaces (outside boundary)
                } else {
                    throw new IllegalArgumentException("Invalid character in map: " + c);
                }
            }
        }

        // Validate closed boundary
        for (int x = 0; x < maxWidth; x++) {
            Position top = new Position(x, 0);
            Position bottom = new Position(x, mapLines.size() - 1);
            Entity topEntity = map.get(top);
            Entity bottomEntity = map.get(bottom);
            if (topEntity == null || !(topEntity instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (top at " + x + ")");
            }
            if (bottomEntity == null || !(bottomEntity instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (bottom at " + x + ")");
            }
        }
        for (int y = 0; y < mapLines.size(); y++) {
            Position left = new Position(0, y);
            Position right = new Position(maxWidth - 1, y);
            Entity leftEntity = map.get(left);
            Entity rightEntity = map.get(right);
            if (leftEntity == null || !(leftEntity instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (left at " + y + ")");
            }
            if (rightEntity == null || !(rightEntity instanceof Wall)) {
                throw new IllegalArgumentException("Map is not a closed boundary (right at " + y + ")");
            }
        }

        if (playerPositions.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required");
        }

        if (destinations.size() != boxPositions.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        for (Position bp : boxPositions) {
            Box box = (Box) map.get(bp);
            if (!playerPositions.containsKey(box.getPlayerId())) {
                throw new IllegalArgumentException("Box references invalid player ID: " + box.getPlayerId());
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return this.map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
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
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            }
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

class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private GameMap gameMap;

    public GameState() {
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>();
        this.history = new ArrayDeque<>();
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.gameMap = null;
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(0);
        this.entities = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.history = new ArrayDeque<>();
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player p = (Player) entry.getValue();
                if (p.getId() == id) {
                    return entry.getKey();
                }
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
        return entities.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity e = entities.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = entities.get(from);
        if (moving == null) return;
        Entity target = entities.get(to);
        if (target instanceof Box) {
            // The actual move (with push) is handled via transition logic in AbstractSokobanGame
            // Here we just relocate the moving entity
            entities.put(to, moving);
            entities.put(from, new Empty());
        } else if (target instanceof Empty || target == null) {
            entities.put(to, moving);
            entities.put(from, new Empty());
        }
    }

    public void checkpoint() {
        // Snapshot current state for undo
        Map<Position, Entity> snapshot = new HashMap<>(this.entities);
        // For simplicity, we record a transition with no moves as a checkpoint marker
        GameStateTransition transition = new GameStateTransition();
        // Store as a special "checkpoint" transition
        transition.setCheckpoint(true);
        this.history.push(transition);
    }

    public void undo() {
        if (this.history.isEmpty()) {
            return;
        }
        GameStateTransition transition = this.history.pop();
        if (transition.isCheckpoint()) {
            return;
        }
        transition.reverse().apply(this);
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

    public Map<Position, Entity> getEntities() {
        return this.entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
    }

    public Deque<GameStateTransition> getHistory() {
        return this.history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }
}

class GameStateTransition {
    private Map<Position, Position> moves;
    private boolean checkpoint;

    public GameStateTransition() {
        this.moves = new HashMap<>();
        this.checkpoint = false;
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
        GameStateTransition t = new GameStateTransition();
        t.moves = reversed;
        t.checkpoint = this.checkpoint;
        return t;
    }

    public void apply(GameState state) {
        for (Map.Entry<Position, Position> entry : this.moves.entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity e = state.getEntity(to);
            if (e != null) {
                state.getEntities().put(from, e);
                state.getEntities().put(to, new Empty());
            }
        }
    }

    public Map<Position, Position> getMoves() {
        return this.moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public boolean isCheckpoint() {
        return this.checkpoint;
    }

    public void setCheckpoint(boolean checkpoint) {
        this.checkpoint = checkpoint;
    }
}

abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public Action() {
        this.initiator = 0;
    }

    public int getInitiator() {
        return this.initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
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

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class Undo extends Action {
    public Undo() {
        super(0);
    }

    public Undo(int initiator) {
        super(initiator);
    }
}

abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public Move() {
        super(0);
    }

    public abstract Position nextPosition(Position currentPosition);
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
        return new Position(currentPosition.x(), currentPosition.y() + 1);
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
        return new Position(currentPosition.x() - 1, currentPosition.y());
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
        return new Position(currentPosition.x() + 1, currentPosition.y());
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
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public ActionResult() {
        this.action = null;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

class Success extends ActionResult {
    public Success() {
        super();
    }

    public Success(Action action) {
        super(action);
    }
}

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

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

interface InputEngine {
    Action fetchAction();
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this.terminalScanner = new Scanner(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, "No input");
        }
        String line = terminalScanner.nextLine().trim();
        if (line.isEmpty()) {
            return new InvalidInput(-1, "Empty input");
        }
        String lower = line.toLowerCase();
        if (lower.equals("exit") || lower.equals("quit")) {
            return new Exit(-1);
        }
        // Player 0: W A S D R
        // Player 1: K H J L U
        switch (line) {
            case "w": case "W":
                return new Up(0);
            case "a": case "A":
                return new Left(0);
            case "s": case "S":
                return new Down(0);
            case "d": case "D":
                return new Right(0);
            case "r": case "R":
                return new Undo(0);
            case "k": case "K":
                return new Up(1);
            case "h": case "H":
                return new Left(1);
            case "j": case "J":
                return new Down(1);
            case "l": case "L":
                return new Right(1);
            case "u": case "U":
                return new Undo(1);
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }
}

interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity e = state.getEntity(pos);
                if (e == null) {
                    sb.append(' ');
                } else if (e instanceof Wall) {
                    sb.append('#');
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    sb.append((char) ('a' + b.getPlayerId()));
                } else if (e instanceof Player) {
                    Player p = (Player) e;
                    sb.append((char) ('A' + p.getId()));
                } else {
                    // Empty
                    if (state.getDestinations().contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                }
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

interface SokobanGame {
    void run();
}

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame() {
        this.state = new GameState();
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            if (this.state.getUndoQuota() == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (this.state.getHistory().isEmpty()) {
                return new Failed(action, "Nothing to undo");
            }
            // Check if top of history is a checkpoint
            GameStateTransition top = this.state.getHistory().peek();
            if (top != null && top.isCheckpoint()) {
                if (this.state.getUndoQuota() != -1) {
                    // Finite quota - consume only if checkpoint exists
                    // For unlimited, don't consume
                }
                this.state.undo();
                return new Success(action);
            } else {
                this.state.undo();
                return new Success(action);
            }
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = action.getInitiator();
            Position currentPos = this.state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);

            // Check bounds
            if (nextPos.x() < 0 || nextPos.x() >= this.state.getMapMaxWidth() ||
                nextPos.y() < 0 || nextPos.y() >= this.state.getMapMaxHeight()) {
                return new Failed(action, "Out of bounds");
            }

            Entity targetEntity = this.state.getEntity(nextPos);
            if (targetEntity instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            }
            if (targetEntity instanceof Player) {
                return new Failed(action, "Blocked by another player");
            }
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push another player's box");
                }
                Position beyondPos = move.nextPosition(nextPos);
                if (beyondPos.x() < 0 || beyondPos.x() >= this.state.getMapMaxWidth() ||
                    beyondPos.y() < 0 || beyondPos.y() >= this.state.getMapMaxHeight()) {
                    return new Failed(action, "Out of bounds");
                }
                Entity beyondEntity = this.state.getEntity(beyondPos);
                if (beyondEntity instanceof Wall || beyondEntity instanceof Player || beyondEntity instanceof Box) {
                    return new Failed(action, "Box is blocked");
                }
                // Push the box
                GameStateTransition transition = new GameStateTransition();
                transition.add(nextPos, beyondPos);
                transition.add(currentPos, nextPos);
                this.state.getHistory().push(transition);
                this.state.move(currentPos, nextPos);
                this.state.move(nextPos, beyondPos);
                this.state.checkpoint();
                return new Success(action);
            } else {
                // Empty space
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                this.state.getHistory().push(transition);
                this.state.move(currentPos, nextPos);
                return new Success(action);
            }
        }
        return new Failed(action, "Unknown action");
    }

    public boolean isExitSpecified() {
        return this.isExitSpecified;
    }

    public void setExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}

class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = new TerminalInputEngine(System.in);
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(this.state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            } else if (result instanceof Success) {
                if (action instanceof Exit) {
                    renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                    break;
                }
                renderingEngine.render(this.state);
                if (this.state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            }
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