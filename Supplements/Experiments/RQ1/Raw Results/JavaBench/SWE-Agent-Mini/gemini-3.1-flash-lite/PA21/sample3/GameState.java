public class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {}
    public GameState(GameBoard gameBoard) { this.gameBoard = gameBoard; }
    public GameState(GameBoard gameBoard, int lives) { this.gameBoard = gameBoard; this.numLives = lives; }

    public int getNumDeaths() { return numDeaths; }
    public void setNumDeaths(int n) { this.numDeaths = n; }
    public int getNumMoves() { return numMoves; }
    public void setNumMoves(int n) { this.numMoves = n; }
    public int getNumLives() { return numLives; }
    public void setNumLives(int n) { this.numLives = n; }
    public int getInitialNumOfGems() { return initialNumOfGems; }
    public void setInitialNumOfGems(int n) { this.initialNumOfGems = n; }
    public GameBoard getGameBoard() { return gameBoard; }
    public void setGameBoard(GameBoard gb) { this.gameBoard = gb; }
    public MoveStack getMoveStack() { return moveStack; }
    public void setMoveStack(MoveStack ms) { this.moveStack = ms; }

    public boolean hasWon() { return getNumGems() == 0; }
    public boolean hasLost() { return !hasUnlimitedLives() && numLives <= 0; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int increaseNumLives(int delta) { return numLives += delta; }
    public int decreaseNumLives(int delta) { return numLives -= delta; }
    public int decrementNumLives() { return --numLives; }
    public int incrementNumMoves() { return ++numMoves; }
    public int incrementNumDeaths() { return ++numDeaths; }
    public int getNumGems() { return gameBoard.getNumGems(); }
    public int getScore() { return 0; }
    public GameBoardController getGameBoardController() { return new GameBoardController(gameBoard); }
    public GameBoardView getGameBoardView() { return new GameBoardView(gameBoard); }
}
