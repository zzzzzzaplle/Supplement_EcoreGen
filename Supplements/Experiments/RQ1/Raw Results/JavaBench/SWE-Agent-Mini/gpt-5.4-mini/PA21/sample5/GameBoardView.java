import java.util.Objects;

/**
 * A read-only view of a {@link GameBoard}.
 */
public class GameBoardView {

    private GameBoard gameBoard;

    public GameBoardView() {
    }

    /**
     * Creates a view of the provided game board.
     *
     * @param gameBoard Game board instance to create a view from.
     */
    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Outputs the textual representation of the game board to {@link System#out}.
     *
     * @param useUnicodeChars If {@code true}, outputs the board elements using Unicode characters (as opposed to ASCII
     *                        characters).
     */
    public void output(final boolean useUnicodeChars) {
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                final Cell cell = gameBoard.getCell(r, c);
                final char ch = useUnicodeChars ? cell.toUnicodeChar() : cell.toASCIIChar();

                System.out.print(ch);
            }
            System.out.println();
        }
    }
}
