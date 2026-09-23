import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        return Position.of(0, 0);
    }

    public Set<Position> getAllPlayerPositions() {
        return new HashSet<>();
    }

    public Entity getEntity(Position position) {
        return new Empty();
    }

    public Set<Position> getDestinations() {
        return new HashSet<>();
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
        return boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getMapMaxHeight() {
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
}
