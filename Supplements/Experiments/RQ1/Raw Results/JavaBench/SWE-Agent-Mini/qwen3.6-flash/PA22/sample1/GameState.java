import java.util.*;

public class GameState {
    private GameMap gameMap;
    private Map<Position, Entity> currentMap;
    private Set<Position> destinations;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private int currentQuota;
    private List<GameStateTransition> transitionStack;
    private List<List<Position>> checkpointStack;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.currentMap = new HashMap<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = gameMap.getEntity(pos);
                if (!(entity instanceof Empty)) {
                    currentMap.put(pos, entity);
                }
            }
        }
        this.undoQuota = gameMap.getUndoLimit();
        this.currentQuota = gameMap.getUndoLimit();
        this.transitionStack = new ArrayList<>();
        this.checkpointStack = new ArrayList<>();
    }

    public Map<Position, Entity> getCurrentMap() {
        return currentMap;
    }

    public int getMaxWidth() {
        return boardWidth;
    }

    public int getMaxHeight() {
        return boardHeight;
    }

    public int getUndoLimit() {
        return undoQuota;
    }

    public Optional<Integer> getUndoLimitOptional() {
        if (undoQuota > 0) {
            return Optional.of(undoQuota);
        }
        return Optional.empty();
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : currentMap.entrySet()) {
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
        for (Map.Entry<Position, Entity> entry : currentMap.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return currentMap.getOrDefault(position, new Empty());
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity entity = getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = getEntity(from);
        Entity target = getEntity(to);

        if (target instanceof Wall) {
            throw new RuntimeException("Cannot move into a wall");
        }
        if (target instanceof Player && entity instanceof Player) {
            throw new RuntimeException("Cannot move into another player");
        }
        if (target instanceof Box) {
            Box box = (Box) target;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (box.getPlayerId() != player.getId()) {
                    throw new RuntimeException("Cannot push a box that doesn't belong to you");
                }
                int dx = (to.x() > from.x()) ? 1 : (to.x() < from.x()) ? -1 : 0;
                int dy = (to.y() > from.y()) ? 1 : (to.y() < from.y()) ? -1 : 0;
                Position boxBehindPos = Position.of(to.x() + dx, to.y() + dy);
                Entity behind = getEntity(boxBehindPos);
                if (behind instanceof Wall || behind instanceof Player || behind instanceof Box) {
                    throw new RuntimeException("Cannot push box into obstacle");
                }
                currentMap.put(boxBehindPos, box);
                currentMap.remove(to);
            } else {
                throw new RuntimeException("Non-player cannot push boxes");
            }
        }

        currentMap.put(to, entity);
        currentMap.remove(from);
    }

    public void checkpoint() {
        List<Position> boxOnDestPositions = new ArrayList<>();
        for (Position dest : destinations) {
            Entity entity = getEntity(dest);
            if (entity instanceof Box) {
                boxOnDestPositions.add(dest);
            }
        }
        checkpointStack.add(boxOnDestPositions);
        if (undoQuota > 0) {
            currentQuota++;
        }
    }

    public void undo() {
        if (!transitionStack.isEmpty()) {
            GameStateTransition transition = transitionStack.remove(transitionStack.size() - 1);
            GameStateTransition reversed = new GameStateTransition();
            for (Map.Entry<Position, Position> entry : transition.getMoves().entrySet()) {
                reversed.add(entry.getValue(), entry.getKey());
            }
            for (Map.Entry<Position, Position> entry : reversed.getMoves().entrySet()) {
                Position pos = entry.getKey();
                Position fromPos = entry.getValue();
                Entity entity = currentMap.remove(pos);
                if (entity != null) {
                    currentMap.put(fromPos, entity);
                }
            }
            if (!checkpointStack.isEmpty()) {
                checkpointStack.remove(checkpointStack.size() - 1);
                if (undoQuota > 0) {
                    currentQuota--;
                }
            }
        }
    }

    public void addToTransition(Position from, Position to) {
        GameStateTransition transition = new GameStateTransition();
        transition.add(from, to);
        transitionStack.add(transition);
    }

    public void recordCheckpoint() {
        checkpoint();
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }
}
