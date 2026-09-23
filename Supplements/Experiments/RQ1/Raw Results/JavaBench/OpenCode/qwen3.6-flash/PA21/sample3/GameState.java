import java.util.ArrayList;
import java.util.List;

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
    private GameController gameController;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.gameBoardController = new GameBoardController(this);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
        this.gameController = new GameController(this);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.gameBoardController = new GameBoardController(this);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
        this.gameController = new GameController(this);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
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

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        return numLives >= 0 && numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        numLives -= delta;
        return numLives;
    }

    public int decrementNumLives() {
        numLives--;
        return numLives;
    }

    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int undoCount = moveStack.getPopCount();
        int boardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undoCount * 2) - (numDeaths * 4);
    }
}
