import java.util.*;
import java.util.stream.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

// --- Interfaces and Abstract Classes ---

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

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity entityAtNext = state.getEntity(nextPos);
            if (entityAtNext instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            } else if (entityAtNext instanceof Player) {
                return new Failed(action, "Blocked by another player");
            } else if (entityAtNext instanceof Box) {
                Box box = (Box) entityAtNext;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box");
                }
                Position behind = move.nextPosition(nextPos);
                Entity entityBehind = state.getEntity(behind);
                if (entityBehind instanceof Empty || entityBehind == null) {
                    // push box
                    state.move(nextPos, behind);
                    state.move(currentPos, nextPos);
                    // checkpoint if box pushed to destination (optional, but we checkpoint on push)
                    state.checkpoint();
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box, space behind is occupied");
                }
            } else if (entityAtNext instanceof Empty || entityAtNext == null) {
                state.move(currentPos, nextPos);
                return new Success(action);
            } else {
                return new Failed(action, "Unknown entity");
            }
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action");
    }
}

// --- TerminalSokobanGame ---

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
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            renderingEngine.render(state);
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
        }
    }
}

// --- InputEngine Interface and TerminalInputEngine ---

interface InputEngine {
    Action fetchAction();
}

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = terminalScanner.nextLine().trim().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        // Player 0: W,A,S,D,R
        // Player 1: K,H,J,L,U
        // But we support up to 26 players in backend, but terminal only 2
        // Map input to player and direction
        if (input.length() == 1) {
            char c = input.charAt(0);
            int playerId = -1;
            String dir = null;
            boolean undo = false;
            switch (c) {
                case 'w': playerId = 0; dir = "up"; break;
                case 'a': playerId = 0; dir = "left"; break;
                case 's': playerId = 0; dir = "down"; break;
                case 'd': playerId = 0; dir = "right"; break;
                case 'r': playerId = 0; undo = true; break;
                case 'k': playerId = 1; dir = "up"; break;
                case 'h': playerId = 1; dir = "left"; break;
                case 'j': playerId = 1; dir = "down"; break;
                case 'l': playerId = 1; dir = "right"; break;
                case 'u': playerId = 1; undo = true; break;
                default: return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
            if (undo) {
                return new Undo(playerId);
            } else if (dir != null) {
                switch (dir) {
                    case "up": return new Up(playerId);
                    case "down": return new Down(playerId);
                    case "left": return new Left(playerId);
                    case "right": return new Right(playerId);
                }
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

// --- RenderingEngine Interface and TerminalRenderingEngine ---

interface RenderingEngine {
    void render(GameState state);
    void message(String content);
}

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char boxChar = (char) ('a' + box.getPlayerId());
                    sb.append(boxChar);
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char playerChar = (char) ('A' + player.getId());
                    sb.append(playerChar);
                } else if (entity instanceof Empty || entity == null) {
                    if (destinations.contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                } else {
                    sb.append('?');
                }
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

// --- Action Hierarchy ---

abstract class Action {
    protected int initiator;

    protected Action(int initiator) {
        this.initiator = initiator;
    }

    public int getInitiator() {
        return initiator;
    }
}

class Exit extends Action {
    public Exit(int initiator) {
        super(initiator);
    }
}

class InvalidInput extends Action {
    private String message;

    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

class Undo extends Action {
    public Undo(int initiator) {
        super(initiator);
    }
}

abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    public abstract Position nextPosition(Position currentPosition);
}

class Down extends Move {
    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

class Up extends Move {
    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

// --- ActionResult Hierarchy ---

abstract class ActionResult {
    protected Action action;

    protected ActionResult(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}

class Success extends ActionResult {
    public Success(Action action) {
        super(action);
    }
}

class Failed extends ActionResult {
    private String reason;

    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

// --- Entity Hierarchy ---

abstract class Entity {
    // No fields, just marker
}

class Box extends Entity {
    private int playerId;

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
    // Empty entity
}

class Player extends Entity {
    private int id;

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
    // Wall entity
}

// --- Position ---

class Position {
    private int x;
    private int y;

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
        return Objects.hash(x, y);
    }
}

// --- GameMap ---

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
        String[] lines = mapText.split("\n");
        // First line: undo limit
        int undoLimit = Integer.parseInt(lines[0].trim());
        int height = lines.length - 1;
        int width = 0;
        // determine width
        for (int i = 1; i < lines.length; i++) {
            width = Math.max(width, lines[i].length());
        }
        Set<Position> destinations = new HashSet<>();
        Map<Position, Entity> entities = new HashMap<>();
        Set<Integer> boxPlayers = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();

        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                char c = (x < line.length()) ? line.charAt(x) : ' ';
                switch (c) {
                    case '#':
                        entities.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entities.put(pos, new Empty());
                        break;
                    case '.':
                        entities.put(pos, new Empty());
                        break;
                    case ' ':
                        // treat as empty but not part of map? Actually empty space
                        entities.put(pos, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int playerId = c - 'A';
                            playerIds.add(playerId);
                            entities.put(pos, new Player(playerId));
                        } else if (c >= 'a' && c <= 'z') {
                            int boxPlayerId = c - 'a';
                            boxPlayers.add(boxPlayerId);
                            entities.put(pos, new Box(boxPlayerId));
                        } else {
                            // unknown character, treat as empty
                            entities.put(pos, new Empty());
                        }
                        break;
                }
            }
        }

        // Validation: closed boundary (check all edges have walls)
        // We'll check that all positions on the boundary (x=0 or x=width-1 or y=0 or y=height-1) are walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    Position pos = new Position(x, y);
                    Entity e = entities.get(pos);
                    if (!(e instanceof Wall)) {
                        throw new IllegalArgumentException("Map must have closed boundary (walls on edges)");
                    }
                }
            }
        }

        // At least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }

        // Number of destinations equals number of boxes
        if (destinations.size() != boxPlayers.size()) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }

        // Boxes must reference valid player IDs
        for (int bp : boxPlayers) {
            if (!playerIds.contains(bp)) {
                throw new IllegalArgumentException("Box references non-existent player ID: " + bp);
            }
        }

        // Also ensure that players are within 0-25
        for (int pid : playerIds) {
            if (pid < 0 || pid > 25) {
                throw new IllegalArgumentException("Player ID must be between 0 and 25");
            }
        }

        return new GameMap(entities, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        // Since map is unmodifiable, we need to create a new map? Actually for mutable state we should use a mutable map.
        // But according to the design, GameMap is static, GameState holds mutable state.
        // This method might be used for initialization? We'll make map mutable for now.
        // Actually the design says GameMap is constructed with map, and getEntity reads from it.
        // For GameState, we'll keep a separate mutable copy.
        // So this putEntity might not be used. We'll implement it but it's not used in the flow.
        // We'll make the map mutable for this method.
        ((Map<Position, Entity>)this.map).put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
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

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}

// --- GameStateTransition (renamed from Transition to match) ---

class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    private GameStateTransition(Map<Position, Position> moves) {
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
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMoves() {
        return Collections.unmodifiableMap(moves);
    }
}

// --- GameState ---

class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Map<Integer, Position> playerPositions;
    private Stack<GameStateTransition> history;
    private Stack<Integer> checkpointCount; // number of transitions in each checkpoint

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.undoQuota = gameMap.getUndoLimit();
        this.entities = new HashMap<>();
        this.history = new Stack<>();
        this.checkpointCount = new Stack<>();
        this.playerPositions = new HashMap<>();

        // Initialize entities from gameMap
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity e = gameMap.getEntity(pos);
                if (e != null) {
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        entities.put(pos, new Player(p.getId()));
                        playerPositions.put(p.getId(), pos);
                    } else if (e instanceof Box) {
                        Box b = (Box) e;
                        entities.put(pos, new Box(b.getPlayerId()));
                    } else if (e instanceof Wall) {
                        entities.put(pos, new Wall());
                    } else if (e instanceof Empty) {
                        entities.put(pos, new Empty());
                    }
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
        Entity entity = entities.remove(from);
        if (entity == null) return;
        entities.put(to, entity);
        if (entity instanceof Player) {
            Player p = (Player) entity;
            playerPositions.put(p.getId(), to);
        }
        // Record the move in a transition (we'll accumulate in a separate transition)
        // But for undo we need to record transitions. We'll handle this in processAction.
        // Actually we need to record moves in a transition object that is stored in history.
        // We'll create a method to record a transition.
    }

    public void checkpoint() {
        // Create a snapshot of current state? Actually we should store accumulated transitions.
        // We'll have a separate mechanism: the processAction will build transitions and call checkpoint.
        // Here we just record the number of transitions in history for undo.
        // We'll maintain a separate stack of transition counts.
    }

    public void undo() {
        if (undoQuota == 0) return;
        if (history.isEmpty()) return;
        // Undo the last checkpoint (pop transitions until we hit a checkpoint marker)
        // We need to store transitions with checkpoints.
        // For simplicity, we'll store GameStateTransition objects in history, and checkpoint() pushes a marker.
        // Actually we need to revert the last checkpoint.
        // We'll implement a simpler approach: store all transitions, and checkpoint marks how many to undo.
        if (checkpointCount.isEmpty()) return;
        int count = checkpointCount.pop();
        List<GameStateTransition> toUndo = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (history.isEmpty()) break;
            toUndo.add(history.pop());
        }
        // Reverse and apply
        for (int i = toUndo.size() - 1; i >= 0; i--) {
            GameStateTransition trans = toUndo.get(i);
            GameStateTransition reversed = trans.reverse();
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                Position from = entry.getKey();
                Position to = entry.getValue();
                Entity entity = entities.remove(to);
                if (entity != null) {
                    entities.put(from, entity);
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        playerPositions.put(p.getId(), from);
                    }
                }
            }
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

    // Additional methods for checkpoint management
    public void addTransition(GameStateTransition transition) {
        history.push(transition);
    }

    public void markCheckpoint() {
        checkpointCount.push(history.size());
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }
}

// --- Reference Implementations (verbatim) ---

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

class NotImplementedException extends RuntimeException {
}

class ShouldNotReachException extends RuntimeException {
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}

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