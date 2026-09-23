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
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private int checkpointCount;

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.entities = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            if (entry.getValue() instanceof Player || entry.getValue() instanceof Box) {
                this.entities.put(entry.getKey(), entry.getValue());
            }
        }
        this.destinations = gameMap.getDestinations();
        this.history = new ArrayDeque<>();
        this.checkpointCount = 0;
        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }
    }

    public GameState() {
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
        Set<Position> playerPositions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                playerPositions.add(entry.getKey());
            }
        }
        return playerPositions;
    }

    public Entity getEntity(Position position) {
        Entity entity = entities.get(position);
        if (entity != null) {
            return entity;
        }
        return gameMap.getEntity(position);
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
        Entity movingEntity = entities.remove(from);
        if (movingEntity != null) {
            Entity existingAtTo = entities.get(to);
            if (existingAtTo instanceof Box) {
                Box box = (Box) existingAtTo;
                entities.remove(to);
                gameMap.getMap();
            }
            entities.put(to, movingEntity);
        }
    }

    public void checkpoint() {
        GameStateTransition transition = new GameStateTransition();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            transition.add(entry.getKey(), entry.getKey());
        }
        history.push(transition);
        checkpointCount++;
    }

    public void undo() {
        if (!history.isEmpty()) {
            if (undoQuota > 0 || undoQuota == -1) {
                GameStateTransition transition = history.pop();
                GameStateTransition reverse = transition.reverse();
                Map<Position, Position> moves = reverse.getMoves();
                Map<Position, Entity> newEntities = new HashMap<>();
                for (Map.Entry<Position, Position> move : moves.entrySet()) {
                    Entity entity = entities.get(move.getKey());
                    if (entity != null) {
                        newEntities.put(move.getValue(), entity);
                    }
                }
                for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
                    if (!moves.containsKey(entry.getKey())) {
                        newEntities.put(entry.getKey(), entry.getValue());
                    }
                }
                entities = newEntities;
                if (undoQuota > 0) {
                    undoQuota--;
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

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public int getCheckpointCount() {
        return checkpointCount;
    }

    public void setCheckpointCount(int checkpointCount) {
        this.checkpointCount = checkpointCount;
    }
}
