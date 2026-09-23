import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private Map<Integer, Position> playerPositions;

    public GameState() {
        this.history = new ArrayDeque<>();
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerPositions = new HashMap<>();
    }

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
        
        // Copy entities from gameMap
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            Entity entity = entry.getValue();
            Position pos = entry.getKey();
            if (entity instanceof Player) {
                Player player = new Player(((Player) entity).getId());
                this.entities.put(pos, player);
                this.playerPositions.put(player.getId(), pos);
            } else if (entity instanceof Box) {
                this.entities.put(pos, new Box(((Box) entity).getPlayerId()));
            } else if (entity instanceof Wall) {
                this.entities.put(pos, new Wall());
            } else if (entity instanceof Empty) {
                this.entities.put(pos, new Empty());
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
            Entity entity = entities.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entities.remove(from);
        if (entity != null) {
            entities.put(to, entity);
            if (entity instanceof Player) {
                playerPositions.put(((Player) entity).getId(), to);
            }
        }
    }

    public void checkpoint() {
        // Create a transition from current state - this is called when a box is pushed
        // The history stores the accumulated moves since last checkpoint
        // Actually, the transitions are stored separately. We use a marker.
        history.push(null); // marker for checkpoint
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        
        // Pop transitions until we hit a checkpoint marker
        GameStateTransition transition = history.pop();
        GameStateTransition accumulated = new GameStateTransition();
        
        while (transition != null) {
            // Apply reverse transition
            GameStateTransition reverse = transition.reverse();
            for (Map.Entry<Position, Position> entry : reverse.getMoves().entrySet()) {
                Position from = entry.getKey();
                Position to = entry.getValue();
                Entity entity = entities.remove(from);
                if (entity != null) {
                    entities.put(to, entity);
                    if (entity instanceof Player) {
                        playerPositions.put(((Player) entity).getId(), to);
                    }
                }
            }
            
            if (history.isEmpty()) {
                break;
            }
            transition = history.pop();
        }
        
        // Consume undo quota if finite and not unlimited
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
}
