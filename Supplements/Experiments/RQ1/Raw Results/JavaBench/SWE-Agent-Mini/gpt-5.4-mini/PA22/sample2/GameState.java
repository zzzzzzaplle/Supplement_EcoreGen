import java.util.Set;

public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    public GameState(GameMap gameMap) {
    }

    public GameState() {
    }

    public Position getPlayerPositionById(int id) {
        throw new NotImplementedException();
    }

    public Set<Position> getAllPlayerPositions() {
        throw new NotImplementedException();
    }

    public Entity getEntity(Position position) {
        throw new NotImplementedException();
    }

    public Set<Position> getDestinations() {
        throw new NotImplementedException();
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
