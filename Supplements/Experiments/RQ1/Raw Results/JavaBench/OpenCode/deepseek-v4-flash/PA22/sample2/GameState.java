import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Deque<GameStateTransition> history;
    private Deque<Integer> checkpointHistory;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        Map<Position, Entity> mutableMap = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            if (entry.getValue() instanceof Wall) {
                mutableMap.put(entry.getKey(), entry.getValue());
            } else {
                mutableMap.put(entry.getKey(), entry.getValue());
            }
        }
        this.entities = mutableMap;
        this.history = new ArrayDeque<>();
        this.checkpointHistory = new ArrayDeque<>();
        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player player = (Player) entry.getValue();
                if (player.getId() == id) {
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
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        Set<Position> destinations = getDestinations();
        for (Position dest : destinations) {
            Entity entity = entities.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entities.get(from);
        if (entity != null) {
            Entity targetEntity = entities.get(to);
            if (targetEntity instanceof Box) {
                GameStateTransition transition = new GameStateTransition();
                Position boxTo = Position.of(
                    2 * to.x() - from.x(),
                    2 * to.y() - from.y()
                );
                transition.add(from, to);
                transition.add(to, boxTo);
                history.push(transition);
                checkpointHistory.push(1);
                entities.remove(from);
                entities.put(to, entity);
                entities.remove(to);
                entities.put(boxTo, targetEntity);
            } else {
                GameStateTransition transition = new GameStateTransition();
                transition.add(from, to);
                history.push(transition);
                entities.remove(from);
                entities.put(to, entity);
            }
        }
    }

    public void checkpoint() {
        if (!checkpointHistory.isEmpty()) {
            return;
        }
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        boolean hasCheckpoint = !checkpointHistory.isEmpty();

        GameStateTransition transition = history.pop();
        GameStateTransition reversed = transition.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = entities.get(from);
            if (entity != null) {
                entities.remove(from);
                entities.put(to, entity);
            }
        }

        if (hasCheckpoint && undoQuota > 0) {
            undoQuota--;
        }
        if (hasCheckpoint) {
            checkpointHistory.pop();
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
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

    public Deque<Integer> getCheckpointHistory() {
        return checkpointHistory;
    }

    public void setCheckpointHistory(Deque<Integer> checkpointHistory) {
        this.checkpointHistory = checkpointHistory;
    }
}
