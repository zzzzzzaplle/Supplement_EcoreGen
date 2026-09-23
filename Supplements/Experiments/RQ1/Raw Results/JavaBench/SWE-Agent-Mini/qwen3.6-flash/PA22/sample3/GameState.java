import java.util.*;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private Map<Position, Entity> board;
    private Set<Position> destinations;
    private List<Map<Position, Entity>> history;
    private List<GameStateTransition> transitions;
    private int checkpointCount;

    public GameState(GameMap gameMap) {
        this.board = new HashMap<>();
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.undoQuota = gameMap.getUndoLimit().orElse(0);
        
        for (Map.Entry<Position, Entity> entry : gameMap.getEntities().entrySet()) {
            this.board.put(new Position(entry.getKey().x(), entry.getKey().y()), entry.getValue());
        }
        
        this.history = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.checkpointCount = 0;
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : board.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public Player getPlayerById(int id) {
        for (Map.Entry<Position, Entity> entry : board.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return (Player) entry.getValue();
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
        Entity entity = board.remove(from);
        if (entity != null) {
            board.put(to, entity);
        }
    }

    public void checkpoint() {
        history.add(new HashMap<>(board));
        checkpointCount++;
    }

    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Cannot undo - no history available");
        }
        
        int lastCheckpoint = history.size() - 1;
        if (lastCheckpoint >= 0) {
            board = new HashMap<>(history.remove(lastCheckpoint));
            checkpointCount--;
            
            if (undoQuota > 0) {
                undoQuota--;
            }
        }
    }
    
    public boolean canUndo() {
        if (history.isEmpty()) {
            return false;
        }
        if (undoQuota == 0) {
            return false;
        }
        if (undoQuota < 0) {
            return true;
        }
        return checkpointCount > 0;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }
    
    public ActionResult tryMove(Position from, Position to) {
        Entity fromEntity = board.get(from);
        if (!(fromEntity instanceof Player)) {
            return new Failed(new InvalidInput(-1, "No player at from position"), "No player at position");
        }
        
        Player player = (Player) fromEntity;
        Entity toEntity = board.get(to);
        
        if (toEntity instanceof Wall) {
            return new Failed(new InvalidInput(player.getId(), "Cannot move into wall"), "Blocked by wall");
        }
        
        if (toEntity instanceof Player) {
            return new Failed(new InvalidInput(player.getId(), "Cannot move into another player"), "Blocked by player");
        }
        
        if (toEntity instanceof Box) {
            Box box = (Box) toEntity;
            if (box.getPlayerId() != player.getId()) {
                return new Failed(new InvalidInput(player.getId(), "Cannot push another player's box"), "Cannot push non-owned box");
            }
            
            int dx = to.x() - from.x();
            int dy = to.y() - from.y();
            Position behindBox = Position.of(to.x() + dx, to.y() + dy);
            
            Entity behindBoxEntity = board.get(behindBox);
            if (behindBoxEntity instanceof Wall || behindBoxEntity instanceof Player || behindBoxEntity instanceof Box) {
                return new Failed(new InvalidInput(player.getId(), "Cannot push box into obstacle"), "Cannot push box into obstacle");
            }
            
            checkpoint();
            
            board.remove(behindBox);
            board.put(to, box);
            
            board.remove(from);
            board.put(to, player);
            
            transitions.add(new GameStateTransition());
            
            return new Success(new Down(player.getId()));
        }
        
        checkpoint();
        
        board.remove(from);
        board.put(to, player);
        
        transitions.add(new GameStateTransition());
        
        return new Success(new Down(player.getId()));
    }
}
