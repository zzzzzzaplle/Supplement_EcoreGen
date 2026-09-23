public class GameState {
    public static int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {}

    public boolean hasWon() { return false; }
    public boolean hasLost() { return false; }
    public boolean hasUnlimitedLives() { return false; }
    public int increaseNumLives(int delta) { return 0; }
    public int decreaseNumLives(int delta) { return 0; }
    public int decrementNumLives() { return 0; }
    public int incrementNumMoves() { return 0; }
    public int incrementNumDeaths() { return 0; }
    public int getNumGems() { return 0; }
    public int getScore() { return 0; }
    public GameBoardController getGameBoardController() { return null; }
    public GameBoardView getGameBoardView() { return null; }
    public GameBoard getGameBoard() { return gameBoard; }
    public MoveStack getMoveStack() { return moveStack; }
    public int getNumLives() { return numLives; }
}
