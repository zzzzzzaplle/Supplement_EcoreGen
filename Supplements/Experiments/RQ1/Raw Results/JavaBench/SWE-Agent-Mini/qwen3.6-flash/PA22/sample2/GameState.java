
import java.util.*;

/**
 * Represents the dynamic game state including board configuration and game history.
 */
public class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> entities;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    private List<GameStateTransition> transitionStack;
    private List<List<GameStateTransition>> checkpointStack;

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        
        Optional<Integer> undoOpt = gameMap.getUndoLimit();
        if (undoOpt.isPresent()) {
            this.undoQuota = undoOpt.get();
        } else {
            this.undoQuota = -1;
        }

        // Deep copy the initial map entities
        this.entities = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : gameMap.getEntityMapInternal().entrySet()) {
            this.entities.put(entry.getKey(), copyEntity(entry.getValue()));
        }

        this.transitionStack = new ArrayList<>();
        this.checkpointStack = new ArrayList<>();
    }

    private Entity copyEntity(Entity entity) {
        if (entity instanceof Player) {
            return new Player(((Player) entity).getId());
        }
        if (entity instanceof Box) {
            return new Box(((Box) entity).getPlayerId());
        }
        if (entity instanceof Wall) {
            return new Wall();
        }
        return new Empty();
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
        return gameMap.getDestinations();
    }

    public boolean isWin() {
        Set<Position> destinations = gameMap.getDestinations();
        if (destinations.isEmpty()) {
            return false;
        }
        for (Position dest : destinations) {
            Entity e = entities.get(dest);
            if (!(e instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entities.remove(from);
        if (entity == null) {
            throw new RuntimeException("No entity at position: " + from);
        }
        entities.put(to, entity);
    }

    public void checkpoint() {
        List<GameStateTransition> checkpoints = new ArrayList<>();
        for (GameStateTransition transition : transitionStack) {
            checkpoints.add(new GameStateTransition(transition.getMovesCopy()));
        }
        checkpointStack.add(checkpoints);
    }

    public void undo() {
        if (checkpointStack.isEmpty()) {
            return;
        }

        List<GameStateTransition> lastCheckpoints = checkpointStack.remove(checkpointStack.size() - 1);

        // Reverse and apply each checkpoint in reverse order
        for (int i = lastCheckpoints.size() - 1; i >= 0; i--) {
            GameStateTransition reversed = lastCheckpoints.get(i).reverse();
            applyTransition(reversed);
        }

        // Clear current transitions
        transitionStack.clear();
    }

    private void applyTransition(GameStateTransition transition) {
        Map<Position, Position> moves = transition.getMovesCopy();
        for (Map.Entry<Position, Position> entry : moves.entrySet()) {
            move(entry.getKey(), entry.getValue());
        }
    }

    public void recordMove(Position from, Position to) {
        if (transitionStack.isEmpty()) {
            transitionStack.add(new GameStateTransition());
        }
        transitionStack.get(transitionStack.size() - 1).add(from, to);
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

    void setUndoQuota(int value) {
        this.undoQuota = value;
    }

    public Optional<Integer> getUndoLimit() {
        if (undoQuota == -1) {
            // -1 means unlimited
            return Optional.empty();
        }
        return Optional.of(undoQuota);
    }
}
