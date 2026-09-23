public class GameState {
    public static int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private int initialBoardSize;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;

    public GameState() {
        this.numLives = UNLIMITED_LIVES;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
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
        if (numLives < 0) {
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
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        this.numLives += delta;
        return this.numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        this.numLives -= delta;
        return this.numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        return ++this.numMoves;
    }

    public int incrementNumDeaths() {
        return ++this.numDeaths;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int undoes = moveStack.getPopCount();
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }
}
