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

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = Integer.MAX_VALUE; // Default to unlimited
        this.initialNumOfGems = gameBoard.getNumGems();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
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
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        if (numLives < 0) {
            return false;
        }
        return numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        return numLives;
    }

    public int decrementNumLives() {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
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
        int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        int undos = moveStack.getPopCount();
        return gameBoard.getNumRows() * gameBoard.getNumCols()
                + (collectedGems * 10)
                - (numMoves * 1)
                - (undos * 2)
                - (numDeaths * 4);
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
