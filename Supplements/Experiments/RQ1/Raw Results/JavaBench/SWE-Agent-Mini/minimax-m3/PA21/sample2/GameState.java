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
    private int undoCount;

    public GameState() {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = 0;
        this.gameBoard = new GameBoard();
        this.moveStack = new MoveStack();
        this.undoCount = 0;
    }

    public GameState(GameBoard gameBoard) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.undoCount = 0;
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.undoCount = 0;
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
        this.numMoves++;
        return this.numMoves;
    }

    public int incrementNumDeaths() {
        this.numDeaths++;
        return this.numDeaths;
    }

    public int getNumGems() {
        if (gameBoard == null) return 0;
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int boardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undoCount * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        if (gameBoardController == null) {
            gameBoardController = new GameBoardController(gameBoard);
        }
        return gameBoardController;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public GameBoardView getGameBoardView() {
        if (gameBoardView == null) {
            gameBoardView = new GameBoardView(gameBoard);
        }
        return gameBoardView;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
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

    public int getUndoCount() {
        return undoCount;
    }

    public void setUndoCount(int undoCount) {
        this.undoCount = undoCount;
    }

    public int getPopCount() {
        return moveStack.getPopCount();
    }
}
