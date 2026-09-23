import java.util.Objects;

public class GameState {

    public static final int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;
    private MoveStack moveStack;

    private int initialBoardSize;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
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

    public int getInitialBoardSize() {
        return initialBoardSize;
    }

    public void setInitialBoardSize(int initialBoardSize) {
        this.initialBoardSize = initialBoardSize;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public boolean hasWon() {
        return gameBoard.getNumGems() <= 0;
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
            return numLives;
        }
        this.numLives += delta;
        return this.numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return numLives;
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
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        int undoes = moveStack.getPopCount();
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }
}
