public class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {}

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() { return gameBoard; }
    public void setGameBoard(GameBoard gameBoard) { this.gameBoard = gameBoard; }

    public MoveResult makeMove(Direction direction) { return null; }
    public void undoMove(MoveResult prevMove) {}
}
