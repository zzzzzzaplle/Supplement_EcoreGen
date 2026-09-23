import java.util.*;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private GameMap map;
    private List<Map<Position, Entity>> history;

    public GameState() {
        this.history = new ArrayList<>();
    }

    public GameState(GameMap map) {
        this.map = map;
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.undoQuota = map.getUndoLimit().orElse(-1);
        this.history = new ArrayList<>();
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : map.getMap().entrySet()) {
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
        for (Map.Entry<Position, Entity> entry : map.getMap().entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return map.getEntity(position);
    }

    public Set<Position> getDestinations() {
        return map.getDestinations();
    }

    public boolean isWin() {
        for (Position destination : map.getDestinations()) {
            Entity entity = map.getEntity(destination);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity fromEntity = map.getEntity(from);
        if (fromEntity instanceof Player) {
            Player player = (Player) fromEntity;
            map.putEntity(from, new Empty());
            Entity toEntity = map.getEntity(to);
            if (toEntity instanceof Empty) {
                map.putEntity(to, player);
            } else if (toEntity instanceof Box) {
                Box box = (Box) toEntity;
                if (box.getPlayerId() != player.getId()) {
                    map.putEntity(from, player);
                    return;
                }
                Position behindBox = Position.of(to.x() + (to.x() - from.x()), to.y() + (to.y() - from.y()));
                if (map.getEntity(behindBox) instanceof Empty) {
                    map.putEntity(behindBox, new Box(player.getId()));
                    map.putEntity(to, player);
                    if (map.getDestinations().contains(behindBox)) {
                        checkpoint();
                    }
                } else {
                    map.putEntity(from, player);
                }
            }
        }
    }

    public void checkpoint() {
        Map<Position, Entity> snapshot = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : map.getMap().entrySet()) {
            Entity orig = entry.getValue();
            if (orig instanceof Box) {
                snapshot.put(entry.getKey(), new Box(((Box) orig).getPlayerId()));
            } else if (orig instanceof Player) {
                snapshot.put(entry.getKey(), new Player(((Player) orig).getId()));
            } else if (orig instanceof Wall) {
                snapshot.put(entry.getKey(), new Wall());
            } else {
                snapshot.put(entry.getKey(), new Empty());
            }
        }
        history.add(snapshot);
    }

    public void undo() {
        if (undoQuota == 0) {
            return;
        }
        int globalUndoLimit = map.getUndoLimit().orElse(-1);
        if (globalUndoLimit == 0 && undoQuota != 0) {
            return;
        }
        if (history.isEmpty()) {
            return;
        }
        if (boxCountAtDestinations() == 0) {
            return;
        }
        Map<Position, Entity> prevState = history.remove(history.size() - 1);
        map = new GameMap(prevState, map.getDestinations(), globalUndoLimit);
        if (undoQuota > 0 && globalUndoLimit > 0) {
            undoQuota--;
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    private int boxCountAtDestinations() {
        int count = 0;
        for (Position destination : map.getDestinations()) {
            Entity entity = map.getEntity(destination);
            if (entity instanceof Box) {
                count++;
            }
        }
        return count;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public List<Map<Position, Entity>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<Position, Entity>> history) {
        this.history = history;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }
}
