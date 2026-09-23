public class GameState {
    public static int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {}
    public GameState(GameBoard gameBoard) { this.gameBoard = gameBoard; this.numLives = UNLIMITED_LIVES; }
    public GameState(GameBoard gameBoard, int numLives) { this.gameBoard = gameBoard; this.numLives = numLives; }

    public int getNumDeaths() { return numDeaths; }
    public void setNumDeaths(int numDeaths) { this.numDeaths = numDeaths; }
    public int getNumMoves() { return numMoves; }
    public void setNumMoves(int numMoves) { this.numMoves = numMoves; }
    public int getNumLives() { return numLives; }
    public void setNumLives(int numLives) { this.numLives = numLives; }
    public int getInitialNumOfGems() { return initialNumOfGems; }
    public void setInitialNumOfGems(int initialNumOfGems) { this.initialNumOfGems = initialNumOfGems; }
    public GameBoard getGameBoard() { return gameBoard; }
    public void setGameBoard(GameBoard gameBoard) { this.gameBoard = gameBoard; }
    public MoveStack getMoveStack() { return moveStack; }
    public void setMoveStack(MoveStack moveStack) { this.moveStack = moveStack; }

    public boolean hasWon() { return false; }
    public boolean hasLost() { return false; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int increaseNumLives(int delta) { return 0; }
    public int decreaseNumLives(int delta) { return 0; }
    public int decrementNumLives() { return 0; }
    public int incrementNumMoves() { return 0; }
    public int incrementNumDeaths() { return 0; }
    public int getNumGems() { return 0; }
    public int getScore() { return 0; }
    public GameBoardController getGameBoardController() { return null; }
    public GameBoardView getGameBoardView() { return null; }
}
