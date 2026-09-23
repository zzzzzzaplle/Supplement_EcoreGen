public class GameState {
    public static final int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
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
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this(gameBoard);
        this.numLives = numLives;
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
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        return numLives;
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

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
}
