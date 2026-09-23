import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Deque<GameStateTransition> history;

    public GameState() {
        this.history = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit();
        this.history = new ArrayDeque<>();
        // Initialize with an empty transition to record moves into
        this.history.push(new GameStateTransition());
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
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
        if (entity != null) {
            gameMap.putEntity(to, entity);
            gameMap.putEntity(from, null);
            // Record the move in the current (top) transition
            if (!history.isEmpty()) {
                history.peek().add(from, to);
            }
        }
    }

    public void checkpoint() {
        // Push a new empty transition for future moves
        // The previous transition now contains moves before this checkpoint
        history.push(new GameStateTransition());
    }

    public void undo() {
        if (!history.isEmpty()) {
            GameStateTransition transition = history.pop();
            if (transition.getMoves().isEmpty() && !history.isEmpty()) {
                // If the popped transition is empty, get the next one
                transition = history.pop();
            }
            if (!transition.getMoves().isEmpty()) {
                GameStateTransition reversed = transition.reverse();
                for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                    Entity entity = gameMap.getEntity(entry.getKey());
                    if (entity != null) {
                        gameMap.putEntity(entry.getValue(), entity);
                        gameMap.putEntity(entry.getKey(), null);
                    }
                }
                // After undo, ensure there's always a transition to record into
                if (history.isEmpty()) {
                    history.push(new GameStateTransition());
                }
            } else {
                // If popped transition was empty, push it back
                history.push(transition);
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

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
