import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private Set<Position> destinations;
    private int undoQuota;
    private Deque<GameStateTransition> history;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.entities = new HashMap<>();
        // Copy entities from gameMap
        for (int y = 0; y < gameMap.getMaxHeight(); y++) {
            for (int x = 0; x < gameMap.getMaxWidth(); x++) {
                Position pos = new Position(x, y);
                Entity entity = gameMap.getEntity(pos);
                if (entity != null) {
                    this.entities.put(pos, entity);
                }
            }
        }
        this.destinations = new HashSet<>(gameMap.getDestinations());
        
        Optional<Integer> undoLimitOpt = gameMap.getUndoLimit();
        if (undoLimitOpt.isPresent()) {
            this.undoQuota = undoLimitOpt.get();
        } else {
            this.undoQuota = 0;
        }
        this.history = new ArrayDeque<>();
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
        if (entity != null) {
            entities.put(to, entity);
            entities.remove(from);
        }
    }

    public void checkpoint() {
        // checkpoint is saved when a box is pushed
        // We save the current state's transition history marker
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        GameStateTransition transition = history.pop();
        GameStateTransition reverse = transition.reverse();
        for (Map.Entry<Position, Position> entry : reverse.getMoves().entrySet()) {
            move(entry.getKey(), entry.getValue());
        }
        if (undoQuota > 0) {
            undoQuota--;
        }
    }

    public int getMapMaxWidth() {
        return gameMap.getMaxWidth();
    }

    public int getMapMaxHeight() {
        return gameMap.getMaxHeight();
    }

    public int getBoardWidth() {
        return gameMap.getMaxWidth();
    }

    public void setBoardWidth(int boardWidth) {
        // Not directly settable from outside
    }

    public int getBoardHeight() {
        return gameMap.getMaxHeight();
    }

    public void setBoardHeight(int boardHeight) {
        // Not directly settable from outside
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
