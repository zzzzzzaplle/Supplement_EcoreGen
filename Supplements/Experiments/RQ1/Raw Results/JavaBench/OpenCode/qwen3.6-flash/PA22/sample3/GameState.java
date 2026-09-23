import java.util.*;

public class GameState {
    private GameMap gameMap;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private List<GameStateTransition> history = new ArrayList<>();
    private List<Integer> checkpointIndices = new ArrayList<>();
    private int currentCheckpointIndex = -1;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitValue();
    }

    public Position getPlayerPositionById(int id) {
        for (Position pos : this.gameMap.getMap().keySet()) {
            Entity entity = this.gameMap.getMap().get(pos);
            if (entity instanceof Player && ((Player) entity).getId() == id) {
                return pos;
            }
        }
        throw new NoSuchElementException("Player not found with id: " + id);
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Position pos : this.gameMap.getMap().keySet()) {
            Entity entity = this.gameMap.getMap().get(pos);
            if (entity instanceof Player) {
                positions.add(pos);
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return this.gameMap.getMap().get(position);
    }

    public Set<Position> getDestinations() {
        return this.gameMap.getDestinations();
    }

    public boolean isWin() {
        for (Position dest : this.gameMap.getDestinations()) {
            Entity entity = this.getEntity(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        GameStateTransition transition = new GameStateTransition();
        Entity fromEntity = this.getEntity(from);
        Entity toEntity = this.getEntity(to);
        boolean isPush = false;

        if (fromEntity instanceof Player && toEntity instanceof Box) {
            // Pushing a box
            int boxPlayerId = ((Box) toEntity).getPlayerId();
            Player player = (Player) fromEntity;
            if (player.getId() != boxPlayerId) {
                throw new IllegalStateException("Player cannot push another player's box");
            }
            Position boxToPosition = new Position(to.x(), to.y() + 1);
            // Determine direction
            int dx = to.x() - from.x();
            int dy = to.y() - from.y();
            boxToPosition = new Position(to.x() + dx, to.y() + dy);
            Entity boxToEntity = this.getEntity(boxToPosition);
            if (boxToEntity == null || boxToEntity instanceof Empty) {
                Entity box = this.getEntity(to);
                this.putEntity(to, fromEntity);
                this.putEntity(from, new Empty());
                this.putEntity(boxToPosition, box);
                transition.add(to, boxToPosition);
                transition.add(from, to);
                isPush = true;

                // Check if box landed on destination - record checkpoint
                if (this.gameMap.getDestinations().contains(boxToPosition)) {
                    this.checkpoint();
                }
            } else {
                throw new IllegalStateException("Cannot push box into non-empty space");
            }
        } else {
            // Regular move
            this.putEntity(to, fromEntity);
            this.putEntity(from, new Empty());
            transition.add(from, to);
        }

        this.history.add(transition);
    }

    public void putEntity(Position position, Entity entity) {
        this.gameMap.getMap().put(position, entity);
    }

    public void checkpoint() {
        this.checkpointIndices.add(this.history.size());
        if (this.undoQuota > 0) {
            this.undoQuota--;
        }
    }

    public void undo() {
        if (this.undoQuota >= 0 && this.checkpointIndices.isEmpty()) {
            throw new IllegalStateException("No undo quota remaining");
        }

        if (!this.checkpointIndices.isEmpty()) {
            int checkpointIndex = this.checkpointIndices.remove(this.checkpointIndices.size() - 1);
            if (this.undoQuota > 0) {
                this.undoQuota++;
            }

            // Apply transitions in reverse order up to the checkpoint
            List<GameStateTransition> transitionsToUndo = this.history.subList(checkpointIndex, this.history.size());
            for (int i = transitionsToUndo.size() - 1; i >= 0; i--) {
                GameStateTransition transition = transitionsToUndo.get(i);
                GameStateTransition reversed = transition.reverse();

                Map<Position, Position> moves = reversed.getMoves();
                Map<Position, Position> finalPositions = new HashMap<>();
                for (Map.Entry<Position, Position> entry : moves.entrySet()) {
                    Position from = entry.getValue();
                    Position to = entry.getKey();
                    finalPositions.put(from, to);
                }

                for (Map.Entry<Position, Position> entry : finalPositions.entrySet()) {
                    Position pos = entry.getKey();
                    Position target = entry.getValue();
                    Entity entity = this.getEntity(pos);
                    if (entity != null) {
                        this.putEntity(target, entity);
                        this.putEntity(pos, new Empty());
                    }
                }
            }

            this.history.subList(checkpointIndex, this.history.size()).clear();
        }
    }

    public int getMapMaxWidth() {
        return this.gameMap.getMaxWidth();
    }

    public int getMapMaxHeight() {
        return this.gameMap.getMaxHeight();
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.undoQuota = gameMap.getUndoLimitValue();
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public int getBoardHeight() {
        return this.boardHeight;
    }
}
