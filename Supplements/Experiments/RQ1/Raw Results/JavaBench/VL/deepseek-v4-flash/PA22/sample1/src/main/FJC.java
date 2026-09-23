import java.util.*;
import java.util.stream.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Interface for the Sokoban game.
 */
interface SokobanGame {
    /**
     * Run the game.
     */
    void run();
}

/**
 * Abstract base class for Sokoban game implementations.
 */
abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    /**
     * Constructor.
     * @param gameState the initial game state
     */
    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    /**
     * Check if the game should stop.
     * @return true if exit is specified or game is won
     */
    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    /**
     * Process an action and return the result.
     * @param action the action to process
     * @return the action result
     */
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        } else if (action instanceof Undo) {
            if (state.getUndoQuota() == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            // Check if there is a checkpoint to undo
            // We'll try to undo; if no checkpoint, it's a failure
            try {
                state.undo();
                return new Success(action);
            } catch (Exception e) {
                return new Failed(action, "Nothing to undo.");
            }
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos;
            try {
                currentPos = state.getPlayerPositionById(initiator);
            } catch (Exception e) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity entityAtNext = state.getEntity(nextPos);
            if (entityAtNext instanceof Empty) {
                // Move player to empty space
                state.move(currentPos, nextPos);
                return new Success(action);
            } else if (entityAtNext instanceof Box) {
                Box box = (Box) entityAtNext;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity entityBehind = state.getEntity(behindPos);
                if (entityBehind instanceof Empty) {
                    // Push box
                    state.move(currentPos, nextPos);
                    state.move(nextPos, behindPos);
                    // Check if box is on a destination
                    if (state.getDestinations().contains(behindPos)) {
                        state.checkpoint();
                    }
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box into obstacle.");
                }
            } else {
                return new Failed(action, "Cannot move into obstacle.");
            }
        }
        return new Failed(action, "Unknown action.");
    }
}

/**
 * Terminal-based Sokoban game implementation.
 */
class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    /**
     * Constructor.
     * @param gameState the initial game state
     * @param inputEngine the input engine
     * @param renderingEngine the rendering engine
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.render(state);
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Success) {
                renderingEngine.render(state);
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                }
            } else if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }
}

/**
 * Interface for input engines.
 */
interface InputEngine {
    /**
     * Fetch an action from the input.
     * @return the action
     */
    Action fetchAction();
}

/**
 * Terminal-based input engine.
 */
class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    /**
     * Constructor.
     * @param terminalStream the input stream
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        String input = terminalScanner.nextLine().trim().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        // Determine player and action
        // Player 0: W/A/S/D/R, Player 1: K/H/J/L/U
        int initiator = -1;
        String direction = null;
        boolean undo = false;
        if (input.length() == 1) {
            char c = input.charAt(0);
            switch (c) {
                case 'w': initiator = 0; direction = "up"; break;
                case 'a': initiator = 0; direction = "left"; break;
                case 's': initiator = 0; direction = "down"; break;
                case 'd': initiator = 0; direction = "right"; break;
                case 'r': initiator = 0; undo = true; break;
                case 'k': initiator = 1; direction = "up"; break;
                case 'h': initiator = 1; direction = "left"; break;
                case 'j': initiator = 1; direction = "down"; break;
                case 'l': initiator = 1; direction = "right"; break;
                case 'u': initiator = 1; undo = true; break;
                default: return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
        } else {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (undo) {
            return new Undo(initiator);
        }
        if (direction != null) {
            switch (direction) {
                case "up": return new Up(initiator);
                case "down": return new Down(initiator);
                case "left": return new Left(initiator);
                case "right": return new Right(initiator);
            }
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

/**
 * Interface for rendering engines.
 */
interface RenderingEngine {
    /**
     * Render the game state.
     * @param state the game state
     */
    void render(GameState state);

    /**
     * Display a message.
     * @param content the message content
     */
    void message(String content);
}

/**
 * Terminal-based rendering engine.
 */
class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    /**
     * Constructor.
     * @param outputStream the output stream
     */
    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char boxChar = (char) ('a' + box.getPlayerId());
                    line.append(boxChar);
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char playerChar = (char) ('A' + player.getId());
                    line.append(playerChar);
                } else if (entity instanceof Empty) {
                    if (destinations.contains(pos)) {
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

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

/**
 * Abstract base class for actions.
 */
abstract class Action {
    protected int initiator;

    /**
     * Constructor.
     * @param initiator the player ID initiating the action
     */
    protected Action(int initiator) {
        this.initiator = initiator;
    }

    /**
     * Get the initiator player ID.
     * @return the player ID
     */
    public int getInitiator() {
        return initiator;
    }
}

/**
 * Action to exit the game.
 */
class Exit extends Action {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Exit(int initiator) {
        super(initiator);
    }
}

/**
 * Action representing invalid input.
 */
class InvalidInput extends Action {
    private String message;

    /**
     * Constructor.
     * @param initiator the player ID
     * @param message the error message
     */
    public InvalidInput(int initiator, String message) {
        super(initiator);
        this.message = message;
    }

    /**
     * Get the error message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}

/**
 * Action to undo the last move.
 */
class Undo extends Action {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Undo(int initiator) {
        super(initiator);
    }
}

/**
 * Abstract base class for move actions.
 */
abstract class Move extends Action {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    protected Move(int initiator) {
        super(initiator);
    }

    /**
     * Calculate the next position from the current position.
     * @param currentPosition the current position
     * @return the next position
     */
    public abstract Position nextPosition(Position currentPosition);
}

/**
 * Move down action.
 */
class Down extends Move {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Down(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() + 1);
    }
}

/**
 * Move left action.
 */
class Left extends Move {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Left(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() - 1, currentPosition.y());
    }
}

/**
 * Move right action.
 */
class Right extends Move {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Right(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x() + 1, currentPosition.y());
    }
}

/**
 * Move up action.
 */
class Up extends Move {
    /**
     * Constructor.
     * @param initiator the player ID
     */
    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) {
        return new Position(currentPosition.x(), currentPosition.y() - 1);
    }
}

/**
 * Abstract base class for action results.
 */
abstract class ActionResult {
    protected Action action;

    /**
     * Constructor.
     * @param action the action that was processed
     */
    protected ActionResult(Action action) {
        this.action = action;
    }

    /**
     * Get the action.
     * @return the action
     */
    public Action getAction() {
        return action;
    }
}

/**
 * Successful action result.
 */
class Success extends ActionResult {
    /**
     * Constructor.
     * @param action the successful action
     */
    public Success(Action action) {
        super(action);
    }
}

/**
 * Failed action result.
 */
class Failed extends ActionResult {
    private String reason;

    /**
     * Constructor.
     * @param action the failed action
     * @param reason the reason for failure
     */
    public Failed(Action action, String reason) {
        super(action);
        this.reason = reason;
    }

    /**
     * Get the failure reason.
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
}

/**
 * Abstract base class for entities on the game board.
 */
abstract class Entity {
}

/**
 * A box entity that belongs to a specific player.
 */
class Box extends Entity {
    private int playerId;

    /**
     * Constructor.
     * @param playerId the ID of the player who owns this box
     */
    public Box(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the player ID.
     * @return the player ID
     */
    public int getPlayerId() {
        return playerId;
    }
}

/**
 * An empty space entity.
 */
class Empty extends Entity {
}

/**
 * A player entity.
 */
class Player extends Entity {
    private int id;

    /**
     * Constructor.
     * @param id the player ID
     */
    public Player(int id) {
        this.id = id;
    }

    /**
     * Get the player ID.
     * @return the player ID
     */
    public int getId() {
        return id;
    }
}

/**
 * A wall entity.
 */
class Wall extends Entity {
}

/**
 * Represents a 2D position on the game board.
 */
class Position {
    private int x;
    private int y;

    /**
     * Constructor.
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x-coordinate.
     * @return the x-coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Get the y-coordinate.
     * @return the y-coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Factory method to create a Position.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return a new Position
     */
    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

/**
 * Represents the game map, including static layout and configuration.
 */
class GameMap {
    private Map<Position, Entity> map;
    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;

    /**
     * Constructor for parsed map.
     * @param maxWidth the maximum width
     * @param maxHeight the maximum height
     * @param destinations the set of destination positions
     * @param undoLimit the undo limit (-1 for unlimited, 0 for none)
     */
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
     * Parse a map from its text representation.
     * @param mapText the text representation of the map
     * @return a parsed GameMap
     */
    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length == 0) throw new IllegalArgumentException("Empty map");
        String firstLine = lines[0].trim();
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(firstLine);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be undo limit number");
        }
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        int maxWidth = 0;
        int maxHeight = 0;
        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);
                maxWidth = Math.max(maxWidth, x + 1);
                maxHeight = Math.max(maxHeight, y);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    case '@':
                        map.put(pos, new Empty());
                        destinations.add(pos);
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int playerId = c - 'A';
                            playerIds.add(playerId);
                            map.put(pos, new Player(playerId));
                        } else if (c >= 'a' && c <= 'z') {
                            int playerId = c - 'a';
                            map.put(pos, new Box(playerId));
                        } else {
                            throw new IllegalArgumentException("Invalid character: " + c);
                        }
                }
            }
        }
        // Validate map
        // Closed boundary check
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position pos = new Position(x, y);
                if (!map.containsKey(pos)) {
                    throw new IllegalArgumentException("Map must be closed boundary (no gaps)");
                }
            }
        }
        // Check walls on edges
        for (int y = 0; y < maxHeight; y++) {
            if (!(map.get(new Position(0, y)) instanceof Wall) || !(map.get(new Position(maxWidth - 1, y)) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have walls on left and right edges");
            }
        }
        for (int x = 0; x < maxWidth; x++) {
            if (!(map.get(new Position(x, 0)) instanceof Wall) || !(map.get(new Position(x, maxHeight - 1)) instanceof Wall)) {
                throw new IllegalArgumentException("Map must have walls on top and bottom edges");
            }
        }
        // At least one player
        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("Map must have at least one player");
        }
        // Count boxes
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (destinations.size() != boxCount) {
            throw new IllegalArgumentException("Number of destinations must equal number of boxes");
        }
        // Validate box player IDs
        for (Entity e : map.values()) {
            if (e instanceof Box) {
                Box box = (Box) e;
                if (!playerIds.contains(box.getPlayerId())) {
                    throw new IllegalArgumentException("Box references non-existent player ID");
                }
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    /**
     * Get the entity at a position.
     * @param position the position
     * @return the entity, or null if out of bounds
     */
    public Entity getEntity(Position position) {
        return map.get(position);
    }

    /**
     * Put an entity at a position.
     * @param position the position
     * @param entity the entity
     */
    public void putEntity(Position position, Entity entity) {
        map.put(position, entity);
    }

    /**
     * Get the set of destinations.
     * @return the destinations
     */
    public Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit.
     * @return Optional containing the undo limit, empty if unlimited
     */
    public Optional<Integer> getUndoLimit() {
        if (undoLimit == -1) return Optional.empty();
        return Optional.of(undoLimit);
    }

    /**
     * Get the set of player IDs.
     * @return the player IDs
     */
    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player) {
                ids.add(((Player) e).getId());
            }
        }
        return ids;
    }

    /**
     * Get the maximum width.
     * @return the maximum width
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Get the maximum height.
     * @return the maximum height
     */
    public int getMaxHeight() {
        return maxHeight;
    }
}

/**
 * Represents the dynamic game state.
 */
class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private List<GameStateTransition> history;
    private Map<Integer, Position> playerPositions;

    /**
     * Constructor from a GameMap.
     * @param gameMap the game map
     */
    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.entities = new HashMap<>();
        this.history = new ArrayList<>();
        this.playerPositions = new HashMap<>();
        // Copy entities from map
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity entity = gameMap.getEntity(pos);
                if (entity != null) {
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        this.entities.put(pos, new Player(p.getId()));
                        playerPositions.put(p.getId(), pos);
                    } else if (entity instanceof Box) {
                        Box b = (Box) entity;
                        this.entities.put(pos, new Box(b.getPlayerId()));
                    } else if (entity instanceof Wall) {
                        this.entities.put(pos, new Wall());
                    } else if (entity instanceof Empty) {
                        this.entities.put(pos, new Empty());
                    }
                }
            }
        }
        // Set undo quota
        Optional<Integer> limitOpt = gameMap.getUndoLimit();
        if (limitOpt.isPresent()) {
            this.undoQuota = limitOpt.get();
        } else {
            this.undoQuota = -1;
        }
    }

    /**
     * No-argument constructor for serialization purposes.
     */
    public GameState() {
    }

    /**
     * Get the position of a player by ID.
     * @param id the player ID
     * @return the position
     */
    public Position getPlayerPositionById(int id) {
        return playerPositions.get(id);
    }

    /**
     * Get all player positions.
     * @return set of player positions
     */
    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>(playerPositions.values());
    }

    /**
     * Get the entity at a position.
     * @param position the position
     * @return the entity
     */
    public Entity getEntity(Position position) {
        return entities.get(position);
    }

    /**
     * Get the set of destinations.
     * @return the destinations
     */
    public Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Check if the game is won.
     * @return true if all destinations are occupied by boxes
     */
    public boolean isWin() {
        for (Position dest : destinations) {
            Entity entity = entities.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Move an entity from one position to another.
     * @param from the source position
     * @param to the destination position
     */
    public void move(Position from, Position to) {
        Entity entity = entities.get(from);
        if (entity == null) throw new IllegalArgumentException("No entity at source position");
        entities.put(to, entity);
        entities.put(from, new Empty());
        if (entity instanceof Player) {
            Player p = (Player) entity;
            playerPositions.put(p.getId(), to);
        }
    }

    /**
     * Record a checkpoint for undo.
     */
    public void checkpoint() {
        GameStateTransition transition = new GameStateTransition();
        history.add(transition);
    }

    /**
     * Undo the last checkpoint.
     */
    public void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("No checkpoints to undo");
        }
        GameStateTransition transition = history.remove(history.size() - 1);
        GameStateTransition reversed = transition.reverse();
        // Apply reversed transition
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = entities.get(from);
            if (entity != null) {
                entities.put(to, entity);
                entities.put(from, new Empty());
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    playerPositions.put(p.getId(), to);
                }
            }
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    /**
     * Get the maximum map width.
     * @return the width
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /**
     * Get the maximum map height.
     * @return the height
     */
    public int getMapMaxHeight() {
        return boardHeight;
    }

    /**
     * Get the undo quota.
     * @return the remaining undo quota (-1 for unlimited)
     */
    public int getUndoQuota() {
        return undoQuota;
    }
}

/**
 * Represents a transition between game states for undo functionality.
 */
class GameStateTransition {
    private Map<Position, Position> moves;

    /**
     * Constructor.
     */
    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    /**
     * Constructor with moves map.
     * @param moves the moves map
     */
    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    /**
     * Add a move from one position to another.
     * @param from the source position
     * @param to the destination position
     */
    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    /**
     * Reverse the transition.
     * @return a new reversed transition
     */
    public GameStateTransition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    /**
     * Get the moves map.
     * @return the moves map
     */
    public Map<Position, Position> getMoves() {
        return moves;
    }
}

/**
 * Factory for creating Sokoban games.
 */
class SokobanGameFactory {
    /**
     * Create a TUI version of the Sokoban game.
     * @param mapFile map file
     * @return The Sokoban game
     * @throws java.io.IOException if mapFile cannot be loaded
     */
    public static SokobanGame createTUIGame(String mapFile) throws java.io.IOException {
        java.nio.file.Path file;
        if (!mapFile.endsWith(".map")) {
            final java.net.URL resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                file = java.nio.file.Path.of(resource.toURI());
            } catch (java.net.URISyntaxException e) {
                throw new RuntimeException("Error loading map:" + mapFile);
            }
        } else {
            file = java.nio.file.Path.of(mapFile);
        }
        final GameMap gameMap = loadGameMap(file);
        return new TerminalSokobanGame(
            new GameState(gameMap),
            new TerminalInputEngine(System.in),
            new TerminalRenderingEngine(System.out)
        );
    }

    /**
     * Load a game map from a file.
     * @param mapFile The file containing the game map
     * @return The parsed game map
     * @throws java.io.IOException When there is an issue loading the file
     */
    public static GameMap loadGameMap(java.nio.file.Path mapFile) throws java.io.IOException {
        final String fileContent = java.nio.file.Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }
}

/**
 * The holder of the entry point of the game.
 */
class Sokoban {
    /**
     * The entry point of the program.
     * @param args The command line args
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
        } catch (java.io.IOException e) {
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
 * Utility class centralizing all display messages and prompts.
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
}