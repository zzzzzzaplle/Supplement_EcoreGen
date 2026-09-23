import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap gameMap;
    private Map<Integer, Position> playerPositions;
    private Map<Position, Entity> currentEntities;
    private Set<Position> destinations;
    private List<GameStateTransition> history;

    public GameState() {
        this.history = new ArrayList<>();
        this.playerPositions = new HashMap<>();
        this.currentEntities = new HashMap<>();
        this.destinations = new HashSet<>();
    }

    public GameState(GameMap gameMap) {
        this.gameMap = gameMap;
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        this.history = new ArrayList<>();
        
        Optional<Integer> undoLimitOpt = gameMap.getUndoLimit();
        if (undoLimitOpt.isPresent() && undoLimitOpt.get() == -1) {
            this.undoQuota = -1;
        } else if (undoLimitOpt.isPresent()) {
            this.undoQuota = undoLimitOpt.get();
        } else {
            this.undoQuota = 0;
        }
        
        this.currentEntities = new HashMap<>();
        this.playerPositions = new HashMap<>();
        
        for (Map.Entry<Position, Entity> entry : gameMap.getMap().entrySet()) {
            Position pos = entry.getKey();
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                Player player = (Player) entity;
                this.playerPositions.put(player.getId(), new Position(pos.x(), pos.y()));
                this.currentEntities.put(new Position(pos.x(), pos.y()), new Player(player.getId()));
            } else if (entity instanceof Box) {
                Box box = (Box) entity;
                this.currentEntities.put(new Position(pos.x(), pos.y()), new Box(box.getPlayerId()));
            } else if (entity instanceof Wall) {
                this.currentEntities.put(new Position(pos.x(), pos.y()), new Wall());
            } else if (entity instanceof Empty) {
                this.currentEntities.put(new Position(pos.x(), pos.y()), new Empty());
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
        return currentEntities.get(position);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public boolean isWin() {
        for (Position dest : destinations) {
            Entity entity = currentEntities.get(dest);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = currentEntities.get(from);
        if (entity != null) {
            currentEntities.put(new Position(to.x(), to.y()), entity);
            currentEntities.remove(from);
            
            if (entity instanceof Player) {
                Player player = (Player) entity;
                playerPositions.put(player.getId(), new Position(to.x(), to.y()));
            }
        }
    }

    public void checkpoint() {
        // Record a checkpoint in history
        GameStateTransition transition = new GameStateTransition();
        history.add(transition);
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        GameStateTransition lastTransition = history.remove(history.size() - 1);
        GameStateTransition reverseTransition = lastTransition.reverse();
        for (Map.Entry<Position, Position> entry : reverseTransition.getMoves().entrySet()) {
            Position from = entry.getKey();
            Position to = entry.getValue();
            Entity entity = currentEntities.get(from);
            if (entity != null) {
                currentEntities.put(new Position(to.x(), to.y()), entity);
                currentEntities.remove(from);
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    playerPositions.put(player.getId(), new Position(to.x(), to.y()));
                }
            }
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

    public List<GameStateTransition> getHistory() {
        return history;
    }

    public void setHistory(List<GameStateTransition> history) {
        this.history = history;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
