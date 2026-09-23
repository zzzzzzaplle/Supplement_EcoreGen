import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> history;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitValue();
        this.entities = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
        for (Map.Entry<Position, Entity> e : this.entities.entrySet()) {
            if (e.getValue() instanceof Player) {
                this.playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
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
        Entity e = entities.get(from);
        entities.remove(from);
        entities.put(to, e);
        if (e instanceof Player) {
            playerPositions.put(((Player) e).getId(), to);
        }
    }

    public void checkpoint() {
        history.push(new GameStateTransition());
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        GameStateTransition t = history.pop();
        GameStateTransition.Transition rev = t.reverse();
        for (Map.Entry<Position, Position> entry : rev.getMoves().entrySet()) {
            move(entry.getKey(), entry.getValue());
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

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }
}
