import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class GameState {

    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Map<Integer, Position> playerPositions;
    private Stack<GameStateTransition> checkpoints;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.gameMap = null;
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.checkpoints = new Stack<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.checkpoints = new Stack<>();
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
        if (gameMap == null) return Collections.emptySet();
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : getDestinations()) {
            Entity e = entities.get(dest);
            if (!(e instanceof Box)) return false;
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity moving = entities.get(from);
        if (moving == null) return;
        Entity target = entities.get(to);
        boolean boxPushed = false;
        if (target instanceof Box) {
            Box b = (Box) target;
            if (moving instanceof Player) {
                Player p = (Player) moving;
                if (b.getPlayerId() != p.getId()) return;
            } else {
                return;
            }
            int dx = Integer.signum(to.x() - from.x());
            int dy = Integer.signum(to.y() - from.y());
            Position beyond = new Position(to.x() + dx, to.y() + dy);
            Entity beyondEntity = entities.get(beyond);
            if (beyondEntity != null && !(beyondEntity instanceof Empty)) return;
            entities.put(beyond, b);
            entities.put(to, new Empty());
            boxPushed = true;
        } else if (!(target == null || target instanceof Empty)) {
            return;
        }
        entities.put(to, moving);
        entities.put(from, new Empty());
        if (moving instanceof Player) {
            playerPositions.put(((Player) moving).getId(), to);
        }
        if (boxPushed) {
            checkpoint();
        }
    }

    public void checkpoint() {
        GameStateTransition transition = new GameStateTransition();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            transition.add(entry.getKey(), entry.getKey());
        }
        checkpoints.push(transition);
    }

    public void undo() {
        if (undoQuota == 0) return;
        if (!checkpoints.isEmpty()) {
            GameStateTransition transition = checkpoints.pop();
            if (undoQuota > 0) {
                undoQuota--;
            }
        }
    }

    public int getMapMaxWidth() {
        if (gameMap != null) return gameMap.getMaxWidth();
        return boardWidth;
    }

    public int getMapMaxHeight() {
        if (gameMap != null) return gameMap.getMaxHeight();
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

    public List<GameStateTransition> getCheckpoints() {
        return new ArrayList<>(checkpoints);
    }

    public void setCheckpoints(Stack<GameStateTransition> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
