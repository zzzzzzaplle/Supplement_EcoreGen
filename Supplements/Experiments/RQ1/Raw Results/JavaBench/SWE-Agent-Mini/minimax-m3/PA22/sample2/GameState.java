import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> board;
    private Set<Position> destinations;
    private Map<Integer, Position> playerPositions;
    private Deque<GameStateTransition> history;
    private Deque<Integer> checkpointStack;
    private GameMap gameMap;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.board = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerPositions = new LinkedHashMap<>();
        this.history = new ArrayDeque<>();
        this.checkpointStack = new ArrayDeque<>();
        this.gameMap = null;
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimit().orElse(0);
        this.board = new HashMap<>();
        for (Map.Entry<Position, Entity> e : gameMap.getMap().entrySet()) {
            this.board.put(e.getKey(), e.getValue());
        }
        this.destinations = new LinkedHashSet<>(gameMap.getDestinations());
        this.playerPositions = new LinkedHashMap<>();
        for (Map.Entry<Position, Entity> e : this.board.entrySet()) {
            if (e.getValue() instanceof Player) {
                Player p = (Player) e.getValue();
                this.playerPositions.put(p.getId(), e.getKey());
            }
        }
        this.history = new ArrayDeque<>();
        this.checkpointStack = new ArrayDeque<>();
    }

    public Position getPlayerPositionById(int id) {
        return playerPositions.get(id);
    }

    public Set<Position> getAllPlayerPositions() {
        return new LinkedHashSet<>(playerPositions.values());
    }

    public Entity getEntity(Position position) {
        return board.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity e = board.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Move an entity from one position to another. Assumes positions are validated by caller.
     */
    public void move(Position from, Position to) {
        Entity e = board.get(from);
        board.put(to, e);
        board.put(from, new Empty());
        if (e instanceof Player) {
            Player p = (Player) e;
            playerPositions.put(p.getId(), to);
        }
    }

    /**
     * Record the latest transition. This method should be called only when a box is pushed.
     */
    public void checkpoint() {
        // when a transition is added that pushes a box, callers will add to history and create a checkpoint
    }

    /**
     * Add a transition to the history. If boxPush is true, mark a checkpoint in the stack.
     */
    public void recordTransition(GameStateTransition transition, boolean boxPush) {
        this.history.push(transition);
        if (boxPush) {
            this.checkpointStack.push(1);
        } else {
            this.checkpointStack.push(0);
        }
    }

    public void undo() {
        if (checkpointStack.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        // The undo must revert until the previous checkpoint boundary.
        // Pop the most recent checkpoint flag and its preceding transitions.
        int lastCheckpoint = 0;
        int popped = 0;
        while (!checkpointStack.isEmpty()) {
            int flag = checkpointStack.pop();
            GameStateTransition t = history.pop();
            applyReverse(t);
            popped++;
            lastCheckpoint = flag;
            if (flag == 1) {
                break;
            }
        }
        if (lastCheckpoint == 1) {
            if (undoQuota > 0) {
                undoQuota--;
            }
        }
    }

    private void applyReverse(GameStateTransition transition) {
        for (Map.Entry<Position, Position> e : transition.getMoves().entrySet()) {
            Position from = e.getKey();
            Position to = e.getValue();
            move(to, from);
        }
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

    public Map<Position, Entity> getBoard() {
        return board;
    }

    public void setBoard(Map<Position, Entity> board) {
        this.board = board;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(Deque<GameStateTransition> history) {
        this.history = history;
    }

    public Deque<Integer> getCheckpointStack() {
        return checkpointStack;
    }

    public void setCheckpointStack(Deque<Integer> checkpointStack) {
        this.checkpointStack = checkpointStack;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
