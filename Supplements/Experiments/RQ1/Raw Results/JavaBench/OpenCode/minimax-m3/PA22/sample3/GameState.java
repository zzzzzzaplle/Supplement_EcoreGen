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
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> checkpoints;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = -1;
        this.gameMap = null;
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.checkpoints = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitRaw();
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.checkpoints = new ArrayDeque<>();
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            this.entities.put(entry.getKey(), entry.getValue());
            if (entry.getValue() instanceof Player) {
                this.playerPositions.put(((Player) entry.getValue()).getId(), entry.getKey());
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
        if (gameMap != null) {
            return gameMap.getDestinations();
        }
        return new HashSet<>();
    }

    public boolean isWin() {
        if (gameMap == null) return false;
        for (Position dest : gameMap.getDestinations()) {
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
        entities.remove(from);
        entities.put(to, moving);
        if (moving instanceof Player) {
            playerPositions.put(((Player) moving).getId(), to);
        }
    }

    public void checkpoint() {
        GameStateTransition transition = new GameStateTransition();
        checkpoints.push(transition);
    }

    public void undo() {
        if (checkpoints.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        GameStateTransition transition = checkpoints.pop();
        GameStateTransition.Transition reversed = transition.reverse();
        for (Map.Entry<Position, Position> e : reversed.getMoves().entrySet()) {
            move(e.getValue(), e.getKey());
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

    public Deque<GameStateTransition> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(Deque<GameStateTransition> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void addTransition(Position from, Position to) {
        if (checkpoints.isEmpty()) {
            checkpoint();
        }
        checkpoints.peek().add(from, to);
    }
}
