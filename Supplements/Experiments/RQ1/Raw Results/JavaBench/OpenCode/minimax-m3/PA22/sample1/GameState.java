import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameState {

    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Map<Integer, Position> playerPositions;
    private List<GameStateTransition> history;
    private GameStateTransition currentTransition;

    public GameState() {
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayList<>();
        this.currentTransition = null;
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayList<>();
        this.currentTransition = null;
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity e = gameMap.getEntity(pos);
                if (e != null) {
                    entities.put(pos, e);
                    if (e instanceof Player) {
                        playerPositions.put(((Player) e).getId(), pos);
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
        if (entities == null) {
            return null;
        }
        return entities.get(position);
    }

    public Set<Position> getDestinations() {
        if (gameMap != null) {
            return gameMap.getDestinations();
        }
        return new HashSet<>();
    }

    public boolean isWin() {
        if (gameMap == null) {
            return false;
        }
        for (Position dest : gameMap.getDestinations()) {
            if (!(entities.get(dest) instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        if (from == null || to == null) {
            return;
        }
        Entity entity = entities.get(from);
        if (entity == null) {
            return;
        }
        if (currentTransition == null) {
            currentTransition = new GameStateTransition();
        }
        Entity existing = entities.get(to);
        entities.remove(from);
        entities.put(to, entity);
        currentTransition.add(from, to);
        if (entity instanceof Player) {
            playerPositions.put(((Player) entity).getId(), to);
        }
        if (existing != null && existing instanceof Player) {
            playerPositions.remove(((Player) existing).getId());
        }
    }

    public void checkpoint() {
        if (currentTransition != null && !currentTransition.getMoves().isEmpty()) {
            history.add(currentTransition);
            currentTransition = new GameStateTransition();
        }
    }

    public void undo() {
        if (undoQuota == 0) {
            return;
        }
        if (history.isEmpty()) {
            if (currentTransition != null && !currentTransition.getMoves().isEmpty()) {
                applyReversed(currentTransition);
                currentTransition = null;
            }
            return;
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
        GameStateTransition transition = history.remove(history.size() - 1);
        applyReversed(transition);
    }

    private void applyReversed(GameStateTransition transition) {
        GameStateTransition reversed = transition.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = entities.get(to);
            entities.remove(to);
            entities.put(from, entity);
            if (entity instanceof Player) {
                playerPositions.put(((Player) entity).getId(), from);
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

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
    }

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public List<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(List<GameStateTransition> history) {
        this.history = history;
    }

    public GameStateTransition getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(GameStateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }
}
