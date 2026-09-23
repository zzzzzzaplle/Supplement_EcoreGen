public class GameState {
    public static final int UNLIMITED_LIVES = -1;
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
        this.gameBoard = gameBoard;
        this.numLives = -1; // unlimited
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public void setNumDeaths(int numDeaths) {
        this.numDeaths = numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getNumLives() {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    public void setInitialNumOfGems(int initialNumOfGems) {
        this.initialNumOfGems = initialNumOfGems;
    }

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        if (hasUnlimitedLives()) {
            return false;
        }
        return numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (!hasUnlimitedLives()) {
            numLives += delta;
        }
        return getNumLives();
    }

    public int decreaseNumLives(int delta) {
        if (!hasUnlimitedLives()) {
            numLives -= delta;
            if (numLives < 0) {
                numLives = 0;
            }
        }
        return getNumLives();
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        return ++numMoves;
    }

    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - getNumGems();
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (moveStack.getPopCount() * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }
}
