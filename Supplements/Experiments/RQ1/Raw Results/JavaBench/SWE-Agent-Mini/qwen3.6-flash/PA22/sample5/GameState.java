import java.util.*;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private List<GameStateTransition> checkpointHistory;
    private GameStateTransition currentTransition;
    private Map<Position, Entity> internalMap;

    public GameState() {
        this.checkpointHistory = new ArrayList<>();
        this.currentTransition = new GameStateTransition();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitValue();
        this.checkpointHistory = new ArrayList<>();
        this.currentTransition = new GameStateTransition();
        this.internalMap = new HashMap<>();
        for (Position pos : gameMap.getMapKeySet()) {
            internalMap.put(pos, gameMap.getEntity(pos));
        }
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : internalMap.entrySet()) {
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
        for (Map.Entry<Position, Entity> entry : internalMap.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return internalMap.get(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : gameMap.getDestinations()) {
            Entity entity = getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity fromEntity = getEntity(from);
        Entity toEntity = getEntity(to);

        if (!(fromEntity instanceof Player)) {
            throw new RuntimeException("Not a player at from position");
        }

        Player player = (Player) fromEntity;

        if (toEntity instanceof Box) {
            Box box = (Box) toEntity;
            if (box.getPlayerId() != player.getId()) {
                throw new RuntimeException("Box cannot be pushed by other player");
            }
            Position behindBox = getPositionBehind(to, from);
            Entity behindEntity = getEntity(behindBox);
            if (behindEntity == null || !(behindEntity instanceof Empty)) {
                throw new RuntimeException("Space behind box is not empty");
            }
            currentTransition.add(to, behindBox);
            internalMap.put(behindBox, box);
            currentTransition.add(from, to);
            internalMap.put(to, player);
            internalMap.put(from, new Empty());
            checkpoint();
        } else if (toEntity instanceof Empty) {
            currentTransition.add(from, to);
            internalMap.put(to, player);
            internalMap.put(from, new Empty());
        } else {
            throw new RuntimeException("Cannot move to " + to);
        }
    }

    private Position getPositionBehind(Position current, Position from) {
        int dx = current.x() - from.x();
        int dy = current.y() - from.y();
        return Position.of(current.x() + dx, current.y() + dy);
    }

    public void checkpoint() {
        GameStateTransition reverse = currentTransition.reverse();
        checkpointHistory.add(reverse);
        currentTransition = new GameStateTransition();
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            throw new RuntimeException("No checkpoints to undo");
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
        GameStateTransition transition = checkpointHistory.remove(checkpointHistory.size() - 1);
        for (Map.Entry<Position, Position> entry : transition.getMoves().entrySet()) {
            Position to = entry.getKey();
            Position from = entry.getValue();
            Entity entity = getEntity(to);
            internalMap.put(from, entity);
            internalMap.put(to, new Empty());
        }
        currentTransition = new GameStateTransition();
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
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

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public List<GameStateTransition> getCheckpointHistory() {
        return checkpointHistory;
    }

    public void setCheckpointHistory(List<GameStateTransition> checkpointHistory) {
        this.checkpointHistory = checkpointHistory;
    }

    public GameStateTransition getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(GameStateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public Map<Position, Entity> getInternalMap() {
        return internalMap;
    }

    public void setInternalMap(Map<Position, Entity> internalMap) {
        this.internalMap = internalMap;
    }

    public void setInternalMapEntry(Position key, Entity value) {
        internalMap.put(key, value);
    }
}
