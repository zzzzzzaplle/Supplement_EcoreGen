import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
    }

    public Position getPlayerPositionById(int id) {
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        return null;
    }

    public Entity getEntity(Position position) {
        return null;
    }

    public Set<Position> getDestinations() {
        return null;
    }

    public boolean isWin() {
        return false;
    }

    public void move(Position from, Position to) {
    }

    public void checkpoint() {
    }

    public void undo() {
    }

    public int getMapMaxWidth() {
        return 0;
    }

    public int getMapMaxHeight() {
        return 0;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public void setUndoQuota(int undoQuota) {
        this.undoQuota = undoQuota;
    }
}
