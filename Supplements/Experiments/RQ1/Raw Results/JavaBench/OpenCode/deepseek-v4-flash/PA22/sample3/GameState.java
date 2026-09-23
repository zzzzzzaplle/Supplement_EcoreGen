import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private Deque<Integer> checkpointIndex;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = gameMap.getDestinations();
        this.entities = new HashMap<>();

        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = gameMap.getEntity(pos);
                if (entity != null) {
                    this.entities.put(pos, entity);
                }
            }
        }

        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }
        this.history = new ArrayDeque<>();
        this.checkpointIndex = new ArrayDeque<>();
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
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return entities.get(position);
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
        Entity entity = entities.get(from);
        if (entity == null) return;

        Entity targetEntity = entities.get(to);

        GameStateTransition transition = new GameStateTransition();

        if (targetEntity instanceof Box) {
            Position behindPos = Position.of(
                2 * to.x() - from.x(),
                2 * to.y() - from.y()
            );
            transition.add(to, behindPos);
            entities.put(behindPos, targetEntity);
            checkpoint();
        }

        transition.add(from, to);
        entities.remove(from);
        entities.put(to, entity);

        history.push(transition);
    }

    public void checkpoint() {
        checkpointIndex.push(history.size());
    }

    public void undo() {
        if (checkpointIndex.isEmpty()) return;
        if (undoQuota != -1 && undoQuota <= 0) return;

        int targetSize = checkpointIndex.pop();
        while (history.size() > targetSize) {
            GameStateTransition transition = history.pop();
            GameStateTransition reversed = transition.reverse();
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                Position from = entry.getKey();
                Position to = entry.getValue();
                Entity entity = entities.get(to);
                if (entity != null) {
                    entities.remove(to);
                    entities.put(from, entity);
                }
            }
        }

        if (undoQuota != -1) {
            undoQuota--;
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

    public Optional<Integer> getUndoLimit() {
        if (undoQuota == -1) {
            return Optional.empty();
        }
        return Optional.of(undoQuota);
    }
}
