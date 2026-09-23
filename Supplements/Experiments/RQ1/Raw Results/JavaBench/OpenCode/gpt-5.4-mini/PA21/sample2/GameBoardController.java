public class GameBoardController {
    public GameBoardController() {
    }

    public MoveResult makeMove(Direction direction) {
        return new Invalid();
    }

    public void undoMove(MoveResult prevMove) {
    }

    public void setGameBoard(GameBoard board) {
    }
}
