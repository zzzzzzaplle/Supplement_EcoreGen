import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {

    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Position, Entity> entityMap;
    private Deque<GameStateTransition> history;
    private GameStateTransition currentTransition;

    public GameState() {
        this.gameMap = new GameMap(0, 0, Collections.emptySet(), 0);
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.entityMap = new HashMap<>();
        this.history = new ArrayDeque<>();
        this.currentTransition = new GameStateTransition();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(0);
        this.entityMap = new HashMap<>();
        this.history = new ArrayDeque<>();
        this.currentTransition = new GameStateTransition();
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            entityMap.put(entry.getKey(), entry.getValue());
        }
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

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Position, Entity> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<Position, Entity> entityMap) {
        this.entityMap = entityMap;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public GameStateTransition getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(GameStateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entityMap.entrySet()) {
            Entity entity = entry.getValue();
            if (entity instanceof Player && ((Player) entity).getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : entityMap.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return entityMap.get(position);
    }

    public Set<Position> getDestinations() {
        if (gameMap != null) {
            return gameMap.getDestinations();
        }
        return Collections.emptySet();
    }

    public boolean isWin() {
        for (Position dest : getDestinations()) {
            Entity entity = entityMap.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entityMap.get(from);
        if (entity == null) {
            return;
        }
        entityMap.remove(from);
        entityMap.put(to, entity);
        if (currentTransition == null) {
            currentTransition = new GameStateTransition();
        }
        currentTransition.add(from, to);
    }

    public void checkpoint() {
        if (currentTransition == null) {
            return;
        }
        if (currentTransition.getMoves() == null || currentTransition.getMoves().isEmpty()) {
            return;
        }
        history.push(currentTransition);
        currentTransition = new GameStateTransition();
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
        GameStateTransition transition = history.pop();
        GameStateTransition reversed = transition.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = entityMap.get(from);
            if (entity != null) {
                entityMap.remove(from);
                entityMap.put(to, entity);
            }
        }
    }

    public int getMapMaxWidth() {
        if (gameMap != null) {
            return gameMap.getMaxWidth();
        }
        return boardWidth;
    }

    public int getMapMaxHeight() {
        if (gameMap != null) {
            return gameMap.getMaxHeight();
        }
        return boardHeight;
    }
}
