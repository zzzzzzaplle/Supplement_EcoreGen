public class GameState {
    public static int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this();
        this.gameBoard = gameBoard;
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard == null ? 0 : gameBoard.getNumGems();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this(gameBoard);
        this.numLives = numLives;
    }

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

    public boolean hasWon() { return gameBoard != null && gameBoard.getNumGems() == 0; }
    public boolean hasLost() { return !hasUnlimitedLives() && numLives <= 0; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int increaseNumLives(int delta) { numLives += delta; return numLives; }
    public int decreaseNumLives(int delta) { numLives -= delta; return numLives; }
    public int decrementNumLives() { return decreaseNumLives(1); }
    public int incrementNumMoves() { return ++numMoves; }
    public int incrementNumDeaths() { return ++numDeaths; }
    public int getNumGems() { return gameBoard == null ? 0 : gameBoard.getNumGems(); }
    public int getScore() { return initialNumOfGems + (getInitialNumOfGems() - getNumGems()) * 10 - (numMoves * 1) - (moveStack == null ? 0 : moveStack.getPopCount() * 2) - (numDeaths * 4); }
    public GameBoardController getGameBoardController() { return new GameBoardController(gameBoard); }
    public GameBoardView getGameBoardView() { return new GameBoardView(gameBoard); }
}
