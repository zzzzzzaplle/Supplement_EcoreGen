import java.util.Objects;

public class GameState {

    public static final int UNLIMITED_LIVES = -1;

    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;

    public GameState(GameBoard gameBoard) {
        this(gameBoard, 3);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = numLives;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
        this.initialNumOfGems = gameBoard.getNumGems();
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
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - getNumGems();
        int undoCount = moveStack.getPopCount();

        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoCount * 2) - (numDeaths * 4);
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public int getNumLives() {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    public Player getPlayer() {
        Entity playerEntity = findPlayerOnBoard();
        if (playerEntity instanceof Player) {
            return (Player) playerEntity;
        }
        return null;
    }

    public boolean undo() {
        if (moveStack.isEmpty()) {
            return false;
        }
        MoveResult move = moveStack.pop();
        if (move instanceof Alive) {
            getGameBoardController().undoMove(move);
            return true;
        }
        return false;
    }

    private Entity findPlayerOnBoard() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    Entity entity = ec.getEntity();
                    if (entity instanceof Player) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
}
