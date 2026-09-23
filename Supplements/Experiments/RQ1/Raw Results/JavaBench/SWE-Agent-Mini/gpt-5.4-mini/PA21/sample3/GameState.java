public class GameState {
    public static int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.moveStack = new MoveStack();
        this.numLives = UNLIMITED_LIVES;
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.moveStack = new MoveStack();
    }

    public int getNumDeaths() { return numDeaths; }
    public void setNumDeaths(int numDeaths) { this.numDeaths = numDeaths; }
    public int getNumMoves() { return numMoves; }
    public void setNumMoves(int numMoves) { this.numMoves = numMoves; }
    public int getNumLives() { return numLives < 0 ? Integer.MAX_VALUE : numLives; }
    public void setNumLives(int numLives) { this.numLives = numLives; }
    public int getInitialNumOfGems() { return initialNumOfGems; }
    public void setInitialNumOfGems(int initialNumOfGems) { this.initialNumOfGems = initialNumOfGems; }
    public GameBoard getGameBoard() { return gameBoard; }
    public void setGameBoard(GameBoard gameBoard) { this.gameBoard = gameBoard; }
    public MoveStack getMoveStack() { return moveStack; }
    public void setMoveStack(MoveStack moveStack) { this.moveStack = moveStack; }

    public boolean hasWon() { return gameBoard != null && gameBoard.getNumGems() == 0; }
    public boolean hasLost() { return !hasUnlimitedLives() && numLives == 0; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int increaseNumLives(int delta) { numLives += delta; return getNumLives(); }
    public int decreaseNumLives(int delta) { numLives -= delta; return getNumLives(); }
    public int decrementNumLives() { return decreaseNumLives(1); }
    public int incrementNumMoves() { return ++numMoves; }
    public int incrementNumDeaths() { return ++numDeaths; }
    public int getNumGems() { return gameBoard == null ? 0 : gameBoard.getNumGems(); }
    public int getScore() { return initialNumOfGems + (initialNumOfGems - getNumGems()) * 10 - numMoves - moveStack.getPopCount() * 2 - numDeaths * 4; }
    public GameBoardController getGameBoardController() { return new GameBoardController(gameBoard); }
    public GameBoardView getGameBoardView() { return new GameBoardView(gameBoard); }
}
