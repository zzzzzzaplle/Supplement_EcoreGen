import java.util.Collections;
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
        return Collections.emptySet();
    }

    public Entity getEntity(Position position) {
        return null;
    }

    public Set<Position> getDestinations() {
        return Collections.emptySet();
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
}
