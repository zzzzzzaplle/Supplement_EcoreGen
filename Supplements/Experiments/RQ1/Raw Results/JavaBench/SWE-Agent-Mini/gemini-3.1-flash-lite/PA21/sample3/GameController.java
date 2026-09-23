public class GameController {
    private GameState gameState;
    public GameController() {}
    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gs) { this.gameState = gs; }
    public MoveResult processMove(Direction direction) { return null; }
    public boolean processUndo() { return false; }
}
