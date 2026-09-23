import java.util.*;

public class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> currentMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Deque<GameStateTransition> checkpointHistory;
    private Deque<GameStateTransition> moveHistory;

    public GameState() {
        this.checkpointHistory = new ArrayDeque<>();
        this.moveHistory = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        Optional<Integer> undoLimitOpt = gameMap.getUndoLimit();
        this.undoQuota = undoLimitOpt.isPresent() ? undoLimitOpt.get() : -1;
        this.currentMap = new HashMap<>();
        // Copy entities from gameMap
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            Position pos = entry.getKey();
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                currentMap.put(pos, new Player(((Player) entity).getId()));
            } else if (entity instanceof Box) {
                currentMap.put(pos, new Box(((Box) entity).getPlayerId()));
            } else if (entity instanceof Wall) {
                currentMap.put(pos, new Wall());
            } else if (entity instanceof Empty) {
                currentMap.put(pos, new Empty());
            }
        }
        this.checkpointHistory = new ArrayDeque<>();
        this.moveHistory = new ArrayDeque<>();
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : currentMap.entrySet()) {
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
        for (Map.Entry<Position, Entity> entry : currentMap.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return currentMap.get(position);
    }

    public Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        Set<Position> destinations = gameMap.getDestinations();
        for (Position dest : destinations) {
            Entity entity = currentMap.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = currentMap.remove(from);
        if (entity != null) {
            currentMap.put(to, entity);
        }
    }

    public void checkpoint() {
        // Store the current state's transition as a checkpoint
        if (!moveHistory.isEmpty()) {
            GameStateTransition transition = new GameStateTransition();
            for (GameStateTransition t : moveHistory) {
                for (Map.Entry<Position, Position> entry : t.getMoves().entrySet()) {
                    transition.add(entry.getKey(), entry.getValue());
                }
            }
            checkpointHistory.push(transition);
            moveHistory.clear();
        }
    }

    public void undo() {
        if (checkpointHistory.isEmpty()) {
            return;
        }
        
        // Consume undo quota if finite
        if (undoQuota > 0) {
            undoQuota--;
        }
        
        GameStateTransition transition = checkpointHistory.pop();
        GameStateTransition reverseTransition = transition.reverse();
        
        // Apply the reverse transition atomically
        // Collect all moves first to avoid intermediate state issues
        Map<Position, Position> reverseMoves = reverseTransition.getMoves();
        Map<Position, Entity> tempMap = new HashMap<>(currentMap);
        
        for (Map.Entry<Position, Position> entry : reverseMoves.entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = tempMap.remove(to);
            if (entity != null) {
                tempMap.put(from, entity);
            }
        }
        
        currentMap = tempMap;
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

    public Map<Position, Entity> getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map<Position, Entity> currentMap) {
        this.currentMap = currentMap;
    }

    public Deque<GameStateTransition> getCheckpointHistory() {
        return checkpointHistory;
    }

    public void setCheckpointHistory(Deque<GameStateTransition> checkpointHistory) {
        this.checkpointHistory = checkpointHistory;
    }

    public Deque<GameStateTransition> getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(Deque<GameStateTransition> moveHistory) {
        this.moveHistory = moveHistory;
    }
}
