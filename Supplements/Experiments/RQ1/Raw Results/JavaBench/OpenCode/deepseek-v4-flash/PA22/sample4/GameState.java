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
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private Deque<GameStateTransition> history;
    private Deque<Integer> checkpointCounts;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.entities = new HashMap<>();
        Map<Position, Entity> map = gameMap.getMap();
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            Position pos = entry.getKey();
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                this.entities.put(pos, new Player(((Player) entity).getId()));
            } else if (entity instanceof Box) {
                this.entities.put(pos, new Box(((Box) entity).getPlayerId()));
            } else if (entity instanceof Wall) {
                this.entities.put(pos, new Wall());
            } else {
                this.entities.put(pos, new Empty());
            }
        }
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.history = new ArrayDeque<>();
        this.checkpointCounts = new ArrayDeque<>();
        if (gameMap.getUndoLimit().isPresent()) {
            this.undoQuota = gameMap.getUndoLimit().get();
        } else {
            this.undoQuota = -1;
        }
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return entry.getKey();
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
        Entity movingEntity = entities.get(from);
        Entity targetEntity = entities.get(to);
        entities.put(to, movingEntity);
        entities.put(from, new Empty());
        boolean boxPushed = targetEntity instanceof Box;
        GameStateTransition transition = new GameStateTransition();
        transition.add(from, to);
        if (boxPushed) {
            Position boxTo = Position.of(
                2 * to.x() - from.x(),
                2 * to.y() - from.y()
            );
            Entity boxEntity = entities.get(to);
            if (boxEntity instanceof Box) {
                entities.put(boxTo, boxEntity);
                entities.put(to, movingEntity);
                entities.put(from, new Empty());
                transition.add(to, boxTo);
            }
        }
        history.push(transition);
        if (boxPushed) {
            checkpoint();
        }
    }

    public void checkpoint() {
        int count = 0;
        for (GameStateTransition t : history) {
            count++;
        }
        checkpointCounts.push(count);
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        if (checkpointCounts.isEmpty()) {
            return;
        }
        if (undoQuota > 0) {
            undoQuota--;
        } else if (undoQuota == 0) {
            return;
        }
        int steps = checkpointCounts.pop();
        GameStateTransition combined = new GameStateTransition();
        for (int i = 0; i < steps && !history.isEmpty(); i++) {
            GameStateTransition t = history.pop();
            for (Map.Entry<Position, Position> entry : t.getMoves().entrySet()) {
                combined.add(entry.getKey(), entry.getValue());
            }
        }
        GameStateTransition reversed = combined.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Entity entity = entities.get(entry.getValue());
            entities.put(entry.getKey(), entity);
            entities.put(entry.getValue(), new Empty());
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

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
    }

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }
}
