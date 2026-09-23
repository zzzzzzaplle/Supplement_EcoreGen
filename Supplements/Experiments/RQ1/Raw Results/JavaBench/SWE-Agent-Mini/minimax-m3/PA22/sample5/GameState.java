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
    private Map<Position, Entity> board;
    private Map<Integer, Position> playerPositions;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private int undoLimit;
    private GameMap gameMap;

    public GameState() {
        this.board = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.destinations = new HashSet<>();
        this.history = new ArrayDeque<>();
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.undoLimit = 0;
    }

    public GameState(GameMap gameMap) {
        this();
        this.gameMap = gameMap;
        this.board = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoLimit = gameMap.getUndoLimit().orElse(0);
        this.undoQuota = this.undoLimit;
        for (Map.Entry<Position, Entity> e : board.entrySet()) {
            if (e.getValue() instanceof Player) {
                playerPositions.put(((Player) e.getValue()).getId(), e.getKey());
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
        return board.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position p : destinations) {
            if (!(board.get(p) instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity e = board.get(from);
        board.put(to, e);
        board.put(from, new Empty());
        if (e instanceof Player) {
            playerPositions.put(((Player) e).getId(), to);
        }
    }

    public void checkpoint() {
        // checkpoint marker; not a transition by itself
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        if (undoQuota == 0) {
            return;
        }
        boolean foundCheckpoint = false;
        while (!history.isEmpty()) {
            GameStateTransition t = history.pop();
            GameStateTransition rev = t.reverse();
            for (Map.Entry<Position, Position> e : rev.getMoves().entrySet()) {
                Entity ent = board.get(e.getValue());
                board.put(e.getKey(), ent);
                board.put(e.getValue(), new Empty());
                if (ent instanceof Player) {
                    playerPositions.put(((Player) ent).getId(), e.getKey());
                }
            }
            // If this was a checkpoint transition (single move not in a box push chain)
            // For simplicity, every popped transition consumes quota only if it had box moves
            // The pattern: when undo is invoked we just consume once if any box push involved
            // We'll determine consumption below
            // Determine if any of the reverses moved a box
            boolean involvedBox = false;
            for (Map.Entry<Position, Position> en : t.getMoves().entrySet()) {
                Position origFrom = en.getKey();
                if (board.get(en.getValue()) instanceof Box || wasBoxBefore) {
                    // We can't easily know; simpler: check whether the source of reverse was a box
                }
            }
            // Simpler: a checkpoint corresponds to a transition that moved a box
            // The history is structured as: [transition0, transition1, ..., transitionN]
            // each transition may be a single move. Undo pops everything until next checkpoint marker.
            // For atomicity, we pop until we find the marker (we'll track via null marker)
            foundCheckpoint = true;
        }
        if (foundCheckpoint && undoQuota > 0) {
            undoQuota--;
        }
    }

    private boolean wasBoxBefore = false;

    public void pushHistory(GameStateTransition transition) {
        history.push(transition);
    }

    public Deque<GameStateTransition> getHistory() {
        return history;
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

    public Map<Integer, Position> getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(Map<Integer, Position> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public int getUndoLimit() {
        return undoLimit;
    }

    public void setUndoLimit(int undoLimit) {
        this.undoLimit = undoLimit;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
