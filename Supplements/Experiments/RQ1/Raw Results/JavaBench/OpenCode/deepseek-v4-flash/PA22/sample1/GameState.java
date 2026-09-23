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
    private Deque<GameStateTransition> history;
    private Deque<Integer> checkpointStack;

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.entities = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }
        this.history = new ArrayDeque<>();
        this.checkpointStack = new ArrayDeque<>();
    }

    public GameState() {
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                if (((Player) entry.getValue()).getId() == id) {
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
            Entity entity = entities.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entities.remove(from);
        entities.put(to, entity);
    }

    public void checkpoint() {
        checkpointStack.push(history.size());
    }

    public void undo() {
        if (checkpointStack.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        int targetSize = checkpointStack.pop();
        if (undoQuota > 0) {
            undoQuota--;
        }
        while (history.size() > targetSize) {
            GameStateTransition transition = history.pop();
            GameStateTransition reverse = transition.reverse();
            for (Map.Entry<Position, Position> entry : reverse.getMoves().entrySet()) {
                Entity entity = entities.remove(entry.getKey());
                if (entity != null) {
                    entities.put(entry.getValue(), entity);
                }
            }
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

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public Deque<Integer> getCheckpointStack() {
        return checkpointStack;
    }

    public void setCheckpointStack(Deque<Integer> checkpointStack) {
        this.checkpointStack = checkpointStack;
    }
}
