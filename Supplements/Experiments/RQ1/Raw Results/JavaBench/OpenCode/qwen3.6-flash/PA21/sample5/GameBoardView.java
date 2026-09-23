import java.util.Objects;

public class GameBoardView {

    private final GameBoard gameBoard;

    public GameBoardView() {
        this.gameBoard = null;
    }

    public GameBoardView(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public void output(boolean useUnicodeChars) {
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                Cell cell = gameBoard.getCell(r, c);
                char ch = useUnicodeChars ? cell.toUnicodeChar() : cell.toASCIIChar();

                System.out.print(ch);
            }
            System.out.println();
        }
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
