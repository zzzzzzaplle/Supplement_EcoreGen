import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GameState {
    private Map<Position, Entity> board;
    private Set<Position> destinations;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private List<Map<Position, Position>> undoStack = new ArrayList<>();
    private boolean hasCheckpoint = false;

    public GameState(GameMap gameMap) {
        this.board = new HashMap<>(gameMap.map);
        this.destinations = new HashSet<>(gameMap.destinations);
        this.boardWidth = gameMap.maxWidth;
        this.boardHeight = gameMap.maxHeight;
        this.undoQuota = gameMap.undoLimit;
    }

    public GameState() {
        this.board = new HashMap<>();
        this.destinations = new HashSet<>();
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : board.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : board.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return board.get(position);
    }

    public void setEntity(Position position, Entity entity) {
        board.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity entity = board.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity fromEntity = board.get(from);
        Entity toEntity = board.get(to);

        if (fromEntity == null || toEntity == null) {
            throw new IllegalStateException("Invalid move: entity missing.");
        }
        if (!(fromEntity instanceof Player)) {
            throw new IllegalStateException("Non-player entity attempted to move.");
        }

        Map<Position, Position> transition = new HashMap<>();
        transition.put(from, to);
        board.remove(from);

        if (toEntity instanceof Box) {
            Box box = (Box) toEntity;
            Player player = (Player) fromEntity;
            if (box.getPlayerId() != player.getId()) {
                throw new IllegalStateException("Cannot push another player's box.");
            }
            Position boxTo = getOppositePosition(to, from);
            Entity boxToEntity = board.get(boxTo);
            if (boxToEntity != null && !(boxToEntity instanceof Empty)) {
                throw new IllegalStateException("Cannot push box into wall, player, or another box.");
            }
            board.remove(to);
            board.put(boxTo, box);
            transition.put(to, boxTo);
            board.put(from, player);
            board.put(to, player);
            board.put(boxTo, box);
            if (destinations.contains(boxTo)) {
                hasCheckpoint = true;
            }
        } else if (toEntity instanceof Empty) {
            board.put(to, fromEntity);
            board.put(from, new Empty());
        } else {
            throw new IllegalStateException("Cannot move to a wall, player, or non-pushable box.");
        }

        undoStack.add(transition);
    }

    public void checkpoint() {
        hasCheckpoint = true;
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        Map<Position, Position> transition = undoStack.remove(undoStack.size() - 1);
        Map<Position, Position> reversedMoves = new GameStateTransition(transition).reverse();
        for (Map.Entry<Position, Position> entry : reversedMoves.entrySet()) {
            Position originalPos = entry.getKey();
            Position revertedPos = entry.getValue();
            Entity e = board.remove(revertedPos);
            if (e == null) {
                board.put(originalPos, new Empty());
            } else {
                board.put(originalPos, e);
            }
        }
        hasCheckpoint = false;
    }

    public boolean canUndo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        if (undoQuota == 0) {
            return false;
        }
        if (undoQuota > 0) {
            return hasCheckpoint;
        }
        return true;
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

    private Position getOppositePosition(Position from, Position to) {
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();
        return Position.of(to.x() + dx, to.y() + dy);
    }
}
