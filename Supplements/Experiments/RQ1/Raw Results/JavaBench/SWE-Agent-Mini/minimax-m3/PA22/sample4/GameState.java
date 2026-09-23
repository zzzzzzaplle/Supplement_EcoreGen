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
    private GameStateTransition currentTransition;
    private Deque<GameStateTransition> checkpoints;

    public GameState() {
        this.boardWidth = 0;
        this.boardHeight = 0;
        this.undoQuota = 0;
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>();
        this.currentTransition = new GameStateTransition();
        this.checkpoints = new ArrayDeque<>();
    }

    public GameState(GameMap gameMap) {
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitRaw();
        this.entities = new HashMap<>(gameMap.getMap());
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.currentTransition = new GameStateTransition();
        this.checkpoints = new ArrayDeque<>();
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> e : entities.entrySet()) {
            if (e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id) {
                return e.getKey();
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> e : entities.entrySet()) {
            if (e.getValue() instanceof Player) {
                positions.add(e.getKey());
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
            Entity e = entities.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity e = entities.get(from);
        entities.put(to, e);
        entities.put(from, new Empty());
    }

    public void checkpoint() {
        checkpoints.push(currentTransition);
        currentTransition = new GameStateTransition();
    }

    public void undo() {
        if (checkpoints.isEmpty()) {
            return;
        }
        GameStateTransition last = checkpoints.pop();
        GameStateTransition reversed = last.reverse();
        for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity e = entities.get(from);
            entities.put(to, e);
            entities.put(from, new Empty());
        }
        currentTransition = new GameStateTransition();
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    public Map<Position, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Position, Entity> entities) {
        this.entities = entities;
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

    public void setDestinations(Set<Position> destinations) {
        this.destinations = destinations;
    }

    public GameStateTransition getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(GameStateTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public Deque<GameStateTransition> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(Deque<GameStateTransition> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
