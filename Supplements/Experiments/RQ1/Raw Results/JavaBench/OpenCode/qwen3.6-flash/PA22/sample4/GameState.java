public class GameState {
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;

    public GameState() {
    }

    public GameState(GameMap gameMap) {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    public void move(Position from, Position to) {
        throw new NotImplementedException();
    }

    public void checkpoint() {
        throw new NotImplementedException();
    }

    public void undo() {
        throw new NotImplementedException();
    }

    public int getMapMaxWidth() {
        throw new NotImplementedException();
    }

    public int getMapMaxHeight() {
        throw new NotImplementedException();
    }
}
