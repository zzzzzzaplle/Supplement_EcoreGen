import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the game state.
 */
public class GameState {

    private GameMap gameMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    private Map<Position, Position> currentTransition;
    private int transitionsToUndo;
    private boolean checkpointPending;
    private boolean transitionToPending;
    private Map<Position, Position> pendingTransition;
    private List<CheckpointRecord> checkpointHistory;

    public GameState() {
        this(new GameMap(0, 0, Collections.emptySet(), 0));
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(-1);

        this.currentTransition = new HashMap<>();
        this.transitionsToUndo = 0;
        this.checkpointPending = false;
        this.transitionToPending = false;
        this.pendingTransition = new HashMap<>();
        this.checkpointHistory = new ArrayList<>();
    }

    public Position getPlayerPositionById(int id) {) {
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player p = (Player) entry.getValue();
                if (p.getId() == id) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return gameMap.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        Set<Position> destinations = gameMap.getDestinations();
        for (Position dest : destinations) {
            Entity entity = gameMap.getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = gameMap.getEntity(from);
        Entity target = gameMap.getEntity(to);

        if (entity == null) {
            return;
        }

        if (entity instanceof Player && target instanceof Box) {
            Position boxTo = new Position(to.x() + (to.x() - from.x()), to.y() + (to.y() - from.y()));
            // Check if box can be pushed
        } else if (entity instanceof Player && target instanceof Empty) {
            gameMap.putEntity(from, new Empty());
            gameMap.putEntity(to, entity);
        }

        currentTransition.put(from, from);
        currentTransition.put(to, to);
        transitionToUndoPending = true;

        if (target instanceof Box) {
            Box box = (Box) target;
            if (boxCanPush(box, from, to)) {
                checkpointPending = true;
            }
        }
    }

    private boolean boxCanPush(Box box, Position from, Position to) {
        // Check if box belongs to the player
        for (Position pos : getAllPlayerPositions()) {
           Player player = (Player) gameMap.getEntity(pos);
            if (player != null && player.getId() == box.getPlayerId()) {
                return true;
            }
        }
        return false;
    }

    public void checkpoint() {
        if (checkpointPending) {
            Checkpoint checkpoint = new Checkpoint(
                cloneMap(gameMap.getMap()),
                new HashSet<>(gameMap.getDestinations()),
                undoQuota
            );
            checkpointHistory.add(checkpoint);
            checkpointPending = false;
        }
    }

    private Map<Position, Entity> cloneMap(Map<Position, Entity> source) {
        Map<Position, Entity> clone = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : source.entrySet()) {
            clone.put(new Position(entry.getKey().x(), entry.getKey().y()), entry.getValue());
        }
        return clone;
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            return;
        }

        int undoLimit = gameMap.getUndoLimit().orElse(-1);
        if (undoLimit == 0) {
            return;
        }

        if (undoLimit != -1 && undoQuota <= 0) {
            return;
        }

        Checkpoint checkpoint = checkpointHistory.remove(checkpointHistory.size() - 1);
        // Recreate GameMap with the checkpointed state
        int maxWidth = 0;
        int maxHeight = 0;
        for (Position pos : checkpoint.map.keySet()) {
            if (pos.x() > maxWidth) maxWidth = pos.x();
            if (pos.y() > maxHeight) maxHeight = pos.y();
        }
        maxWidth++;
        maxHeight++;
        gameMap = new GameMap(maxWidth, maxHeight, checkpoint.destinations, undoLimit);
        // Copy entities back
        for (Map.Entry<Position, Entity> entry : checkpoint.map.entrySet()) {
            gameMap.putEntity(entry.getKey(), entry.getValue());
        }
        undoQuota = checkpoint.undoQuota;
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

    public Map<Position, Position> getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(Map<Position, Position> currentTransition) {
        this.currentTransition = currentTransition;
    }

    public int getTransitionsToUndo() {
        return transitionsToUndo;
    }

    public void setTransitionsToUndo(int transitionsToUndo) {
        this.transitionsToUndo = transitionsToUndo;
    }

    public boolean isCheckpointPending() {
        return checkpointPending;
    }

    public void setCheckpointPending(boolean checkpointPending) {
        this.checkpointPending = checkpointPending;
    }

    public boolean isTransitionToUndoPending() {
        return transitionToUndoPending;
    }

    public void setTransitionToUndoPending(boolean transitionToUndoPending) {
        this.transitionToUndoPending = transitionToUndoPending;
    }

    public Map<Position, Position> getPendingTransition() {
        return pendingTransition;
    }

    public void setPendingTransition(Map<Position, Position> pendingTransition) {
        this.pendingTransition = pendingTransition;
    }

    public List<Checkpoint> getCheckpointHistory() {
        return checkpointHistory;
    }

    public void setCheckpointHistory(List<Checkpoint> checkpointHistory) {
        this.checkpointHistory = checkpointHistory;
    }

    private static class Checkpoint {
        private Map<Position, Entity> map;
        private Set<Position> destinations;
        private int undoQuota;

        public Checkpoint(Map<Position, Entity> map, Set<Position> destinations, int undoQuota) {
            this.map = map;
            this.destinations = destinations;
            this.undoQuota = undoQuota;
        }

        public Map<Position, Entity> getMap() {
            return map;
        }

        public Set<Position> getDestinations() {
            return destinations;
        }

        public int getUndoQuota() {
            return undoQuota;
        }
    }
}
