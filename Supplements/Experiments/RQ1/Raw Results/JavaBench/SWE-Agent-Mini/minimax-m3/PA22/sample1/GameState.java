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
    private Map<Position, Entity> board;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> history;
    private boolean checkpointPending;

    public GameState() {
        this.board = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.board = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.history = new ArrayDeque<>();
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }

        for (Map.Entry<Position, Entity> e : gameMap.getMap().entrySet()) {
            this.board.put(e.getKey(), e.getValue());
            if (e.getValue() instanceof Player) {
                this.playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
            }
        }
    }

    public Position getPlayerPositionById(int id) {
        return this.playerPositions.get(id);
    }

    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>(this.playerPositions.values());
    }

    public Entity getEntity(Position position) {
        return this.board.get(position);
    }

    public Set<Position> getDestinations() {
        return this.gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : this.gameMap.getDestinations()) {
            Entity e = this.board.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity e = this.board.get(from);
        this.board.put(to, e);
        this.board.put(from, new Empty());
        if (e instanceof Player) {
            this.playerPositions.put(((Player) e).getId(), to);
        }
    }

    public void checkpoint() {
        this.checkpointPending = true;
    }

    public void commitCheckpoint() {
        if (this.checkpointPending) {
            // no-op
            this.checkpointPending = false;
        }
    }

    public void undo() {
        if (this.history.isEmpty()) {
            return;
        }
        GameStateTransition t = this.history.pop();
        GameStateTransition rev = t.reverse();
        for (Map.Entry<Position, Position> e : rev.getMoves().entrySet()) {
            Entity ent = this.board.get(e.getValue());
            this.board.put(e.getKey(), ent);
            this.board.put(e.getValue(), new Empty());
            if (ent instanceof Player) {
                this.playerPositions.put(((Player) ent).getId(), e.getKey());
            }
        }
    }

    public boolean hasCheckpoint() {
        return !this.history.isEmpty();
    }

    public void recordTransition(GameStateTransition t) {
        this.history.push(t);
    }

    public int getMapMaxWidth() {
        return this.boardWidth;
    }

    public int getMapMaxHeight() {
        return this.boardHeight;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Position, Entity> getBoard() {
        return this.board;
    }

    public void setBoard(Map<Position, Entity> board) {
        this.board = board;
    }

    public Map<Integer, Position> getPlayerPositionsMap() {
        return this.playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Deque<GameStateTransition> getHistory() {
        return this.history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getBoardHeight() {
        return this.boardHeight;
    }

    public void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    public int getUndoQuota() {
        return this.undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }

    public void consumeUndoQuota() {
        if (this.undoQuota > 0) {
            this.undoQuota--;
        }
    }
}
