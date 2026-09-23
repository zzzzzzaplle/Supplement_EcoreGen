import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameState {

    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Map<Integer, Position> playerPositions;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private Deque<GameStateTransition> checkpoints;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = -1;
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.destinations = new HashSet<>();
        this.history = new ArrayDeque<>();
        this.checkpoints = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.playerPositions = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : this.entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player p = (Player) entry.getValue();
                this.playerPositions.put(p.getId(), entry.getKey());
            }
        }
        this.history = new ArrayDeque<>();
        this.checkpoints = new ArrayDeque<>();
    }

    public Position getPlayerPositionById(int id) {
        return this.playerPositions.get(id);
    }

    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>(this.playerPositions.values());
    }

    public Entity getEntity(Position position) {
        return this.entities.get(position);
    }

    public Set<Position> getDestinations() {
        return this.destinations;
    }

    public boolean isWin() {
        for (Position dest : this.destinations) {
            Entity e = this.entities.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = this.entities.get(from);
        if (moving == null) return;
        this.entities.remove(from);
        this.entities.put(to, moving);
        if (moving instanceof Player) {
            this.playerPositions.put(((Player) moving).getId(), to);
        }
    }

    public void checkpoint() {
        if (!this.history.isEmpty()) {
            this.checkpoints.push(this.history.pop());
        }
    }

    public void undo() {
        if (this.undoQuota == 0) {
            return;
        }
        if (this.checkpoints.isEmpty()) {
            return;
        }
        if (this.undoQuota > 0) {
            this.undoQuota--;
        }
        GameStateTransition transition = this.checkpoints.pop();
        GameStateTransition.Transition reversed = transition.reverse();
        Map<Position, Position> moves = reversed.getMoves();
        // apply reversed moves atomically
        Map<Position, Entity> snapshot = new LinkedHashMap<>();
        for (Map.Entry<Position, Position> e : moves.entrySet()) {
            Position to = e.getKey();
            Position from = e.getValue();
            Entity ent = this.entities.get(to);
            if (ent != null) {
                snapshot.put(from, ent);
                this.entities.remove(to);
            }
        }
        for (Map.Entry<Position, Entity> e : snapshot.entrySet()) {
            this.entities.put(e.getKey(), e.getValue());
            if (e.getValue() instanceof Player) {
                this.playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
            }
        }
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

    public Map<Integer, Position> getPlayerPositions() {
        return this.playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Deque<GameStateTransition> getHistory() {
        return this.history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public Deque<GameStateTransition> getCheckpoints() {
        return this.checkpoints;
    }

    public void setCheckpoints(Deque<GameStateTransition> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void addTransition(GameStateTransition transition) {
        this.history.push(transition);
    }
}
