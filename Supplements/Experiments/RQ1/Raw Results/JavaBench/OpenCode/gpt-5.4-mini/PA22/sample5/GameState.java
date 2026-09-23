import java.util.*;

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
