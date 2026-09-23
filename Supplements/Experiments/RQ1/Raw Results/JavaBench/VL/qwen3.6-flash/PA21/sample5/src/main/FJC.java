import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileAlreadyExistsException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 2D grid coordinate.
 */
class Position {
    private int row;
    private int col;

    public Position() {
        this.row = 0;
        this.col = 0;
    }

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns a new Position offset by the given deltas.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Returns a new Position offset by the given PositionOffset.
     */
    public Position offsetBy(PositionOffset offset) {
        return new Position(this.row + offset.getDRow(), this.col + offset.getDCol());
    }

    /**
     * Returns a new Position offset by the given deltas, or null if out of bounds.
     */
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    /**
     * Returns a new Position offset by the given PositionOffset, or null if out of bounds.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}

/**
 * Represents a delta offset for a position.
 */
class PositionOffset {
    private int dRow;
    private int dCol;

    public PositionOffset() {
        this.dRow = 0;
        this.dCol = 0;
    }

    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {
        return dRow;
    }

    public void setDRow(int dRow) {
        this.dRow = dRow;
    }

    public int getDCol() {
        return dCol;
    }

    public void setDCol(int dCol) {
        this.dCol = dCol;
    }
}

/**
 * Interface for elements that can be rendered on the board.
 */
interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

/**
 * Enum representing movement directions.
 */
enum Direction {
    UP(new PositionOffset(-1, 0)),
    DOWN(new PositionOffset(1, 0)),
    LEFT(new PositionOffset(0, -1)),
    RIGHT(new PositionOffset(0, 1));

    private final PositionOffset offset;

    Direction(PositionOffset offset) {
        this.offset = offset;
    }

    public PositionOffset getOffset() {
        return offset;
    }

    public int getRowOffset() {
        return offset.getDRow();
    }

    public int getColOffset() {
        return offset.getDCol();
    }
}

/**
 * Abstract class for cells in the game board.
 */
abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
        this.position = new Position();
    }

    public Cell(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}

/**
 * A cell that can contain an entity.
 */
class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {
        super();
        this.entity = null;
    }

    public EntityCell(Position position) {
        super(position);
        this.entity = null;
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(Entity newEntity) {
        Entity oldEntity = this.entity;
        if (oldEntity != null) {
            oldEntity.setOwner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return oldEntity;
    }

    public Entity setentity(Entity newEntity) {
        Entity prev = this.entity;
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return prev;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}

/**
 * A special stop cell that can only contain a Player.
 */
class StopCell extends EntityCell {
    public StopCell() {
        super();
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    @Override
    public Entity setEntity(Entity newEntity) {
        return super.setEntity(newEntity);
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        return (Player) setEntity(newPlayer);
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
}

/**
 * A wall cell.
 */
class Wall extends Cell {
    public Wall() {
        super();
    }

    public Wall(Position position) {
        super(position);
    }

    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
}

/**
 * Abstract class for entities on the board.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
        this.owner = null;
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell owner) {
        this.owner = owner;
        return owner;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }
}

/**
 * A gem entity to be collected.
 */
class Gem extends Entity {
    public Gem() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u25C7';
    }

    @Override
    public char toASCIIChar() {
        return '*';
    }
}

/**
 * A mine entity that causes death.
 */
class Mine extends Entity {
    public Mine() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u26A0';
    }

    @Override
    public char toASCIIChar() {
        return 'X';
    }
}

/**
 * An extra life entity.
 */
class ExtraLife extends Entity {
    public ExtraLife() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u2661';
    }

    @Override
    public char toASCIIChar() {
        return 'L';
    }
}

/**
 * The player entity.
 */
class Player extends Entity {
    public Player() {
        super();
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }
}

/**
 * Base class for move results.
 */
abstract class MoveResult {
    protected Position newPosition;

    public MoveResult() {
        this.newPosition = new Position();
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }
}

/**
 * Result for an invalid move.
 */
class Invalid extends MoveResult {
    public Invalid() {
        super();
    }
}

/**
 * Result for a valid move where the player is alive.
 */
class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}

/**
 * Base class for valid moves.
 */
class Valid extends MoveResult {
    protected Position origPosition;

    public Valid() {
        super();
        this.origPosition = new Position();
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}

/**
 * Result for a move that resulted in death.
 */
class Dead extends Valid {
    private Position minePosition;

    public Dead() {
        super();
        this.minePosition = new Position();
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}

/**
 * Represents the game board grid.
 */
class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;
    private Player player;

    public GameBoard() {
        this.numRows = 0;
        this.numCols = 0;
        this.board = new Cell[0][0];
        this.player = null;
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = null;
        
        // Find player position
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        this.player = (Player) entity;
                    }
                }
            }
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Cell[] getRow(int r) {
        if (r < 0 || r >= numRows) {
            return new Cell[0];
        }
        return board[r];
    }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return null;
        }
        return board[r][c];
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        Cell cell = getCell(r, c);
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }
}

/**
 * Stack to keep track of moves for undo functionality.
 */
class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new LinkedList<>();
    }

    public int getPopCount() {
        return popCount;
    }

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(Deque<MoveResult> stack) {
        this.stack = stack;
    }

    public void push(MoveResult move) {
        this.stack.push(move);
    }

    public MoveResult pop() {
        MoveResult result = this.stack.pop();
        this.popCount++;
        return result;
    }

    public MoveResult peek() {
        return this.stack.peek();
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
}

/**
 * Controller for handling game board logic.
 */
class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = new GameBoard();
    }

    public GameBoardController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Makes a move in the specified direction.
     */
    public MoveResult makeMove(Direction direction) {
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        EntityCell playerCell = null;
        Position playerPos = null;

        // Find player's current cell
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity == player) {
                        playerCell = (EntityCell) cell;
                        playerPos = cell.getPosition();
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        int currentRow = playerPos.getRow();
        int currentCol = playerPos.getCol();
        
        Position finalPos = new Position(currentRow, currentCol);
        boolean moved = false;
        boolean hitMine = false;
        Position minePos = null;
        
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        // Slide step by step
        while (true) {
            int nextRow = currentRow + dRow;
            int nextCol = currentCol + dCol;

            // Check boundary
            if (nextRow < 0 || nextRow >= gameBoard.getNumRows() || nextCol < 0 || nextCol >= gameBoard.getNumCols()) {
                // Hit boundary, stop
                break;
            }

            Cell nextCell = gameBoard.getCell(nextRow, nextCol);
            
            // Check for Wall
            if (nextCell instanceof Wall) {
                // Stop before wall, do not move into it
                break;
            }

            // Move into the cell
            currentRow = nextRow;
            currentCol = nextCol;
            moved = true;
            finalPos = new Position(currentRow, currentCol);

            // Check if cell is a StopCell (and not a wall/boundary, already checked)
            if (nextCell instanceof StopCell) {
                // Stop at StopCell
                break;
            }

            // Check for Entity
            if (nextCell instanceof EntityCell) {
                Entity entity = ((EntityCell) nextCell).getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = new Position(currentRow, currentCol);
                    break;
                } else if (entity instanceof Gem) {
                    collectedGems.add(new Position(currentRow, currentCol));
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(currentRow, currentCol));
                }
            }
        }

        if (!moved) {
            return new Invalid();
        }

        if (hitMine) {
            Dead deadResult = new Dead();
            deadResult.setOrigPosition(playerPos);
            deadResult.setNewPosition(playerPos); // Position remains original
            deadResult.setMinePosition(minePos);
            return deadResult;
        } else {
            Alive aliveResult = new Alive();
            aliveResult.setOrigPosition(playerPos);
            aliveResult.setNewPosition(finalPos);
            aliveResult.setCollectedGems(collectedGems);
            aliveResult.setCollectedExtraLives(collectedExtraLives);
            
            // Update board state: remove collected items, move player
            // Remove player from old position
            EntityCell oldPlayerCell = (EntityCell) gameBoard.getCell(playerPos.getRow(), playerPos.getCol());
            if (oldPlayerCell != null) {
                oldPlayerCell.setEntity(null);
            }

            // Remove collected items
            for (Position gemPos : collectedGems) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
                if (gemCell != null) {
                    gemCell.setEntity(null);
                }
            }
            for (Position lifePos : collectedExtraLives) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos.getRow(), lifePos.getCol());
                if (lifeCell != null) {
                    lifeCell.setEntity(null);
                }
            }

            // Place player at new position
            EntityCell newPlayerCell = (EntityCell) gameBoard.getCell(finalPos.getRow(), finalPos.getCol());
            if (newPlayerCell != null) {
                newPlayerCell.setEntity(player);
            }

            return aliveResult;
        }
    }
}

/**
 * View for the game board.
 */
class GameBoardView {
    private final GameBoard gameBoard;

    /**
     * Creates a view of the provided game board.
     *
     * @param gameBoard Game board instance to create a view from.
     */
    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Outputs the textual representation of the game board to {@link System#out}.
     *
     * @param useUnicodeChars If {@code true}, outputs the board elements using Unicode characters (as opposed to ASCII
     *                        characters).
     */
    public void output(final boolean useUnicodeChars) {
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                final Cell cell = gameBoard.getCell(r, c);
                final char ch = useUnicodeChars ? cell.toUnicodeChar() : cell.toASCIIChar();

                System.out.print(ch);
            }
            System.out.println();
        }
    }
}

/**
 * Holds the complete state of the game.
 */
class GameState {
    public static final int UNLIMITED_LIVES = -1;
    
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;
    private MoveStack moveStack;

    public GameState() {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 3; // Default lives
        this.initialNumOfGems = 0;
        this.moveStack = new MoveStack();
        
        GameBoard board = new GameBoard();
        this.gameBoardController = new GameBoardController(board);
        this.gameBoardView = new GameBoardView(board);
        
        // Count initial gems
        this.initialNumOfGems = board.getNumGems();
    }

    public GameState(GameBoard gameBoard) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 3;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
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

    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
        this.gameBoardView = new GameBoardView(gameBoardController.getGameBoard());
    }

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public GameBoard getGameBoard() {
        return gameBoardController.getGameBoard();
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && getNumLives() <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int getNumGems() {
        return gameBoardController.getGameBoard().getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int undos = moveStack.getPopCount();
        return (gameBoardController.getGameBoard().getNumRows() * gameBoardController.getGameBoard().getNumCols()) +
               (collectedGems * 10) -
               (numMoves * 1) -
               (undos * 2) -
               (numDeaths * 4);
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
}

/**
 * Controller for the game logic.
 */
class GameController {
    private GameState gameState;

    public GameController() {
        this.gameState = new GameState();
    }

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        
        if (result instanceof Invalid) {
            return result;
        }
        
        if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            
            // Apply collected ExtraLives
            for (Position pos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            
            gameState.getMoveStack().push(result);
            return result;
        }
        
        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            return result;
        }
        
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        
        MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            Position oldPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();
            
            // Revert player position
            Player player = gameState.getGameBoardController().getGameBoard().getPlayer();
            if (player != null) {
                // Remove player from current position
                EntityCell currentCell = gameState.getGameBoardController().getGameBoard().getEntityCell(newPos.getRow(), newPos.getCol());
                if (currentCell != null) {
                    currentCell.setEntity(null);
                }
                
                // Place player at old position
                EntityCell oldCell = gameState.getGameBoardController().getGameBoard().getEntityCell(oldPos.getRow(), oldPos.getCol());
                if (oldCell != null) {
                    oldCell.setEntity(player);
                }
            }
            
            // Restore collected Gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameState.getGameBoardController().getGameBoard().getEntityCell(gemPos.getRow(), gemPos.getCol());
                if (gemCell != null && gemCell.getEntity() == null) {
                    gemCell.setEntity(new Gem());
                }
            }
            
            // Restore collected ExtraLives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = gameState.getGameBoardController().getGameBoard().getEntityCell(lifePos.getRow(), lifePos.getCol());
                if (lifeCell != null && lifeCell.getEntity() == null) {
                    lifeCell.setEntity(new ExtraLife());
                    gameState.decreaseNumLives(1); // Reverse the life gain
                }
            }
            
            return true;
        }
        
        return false;
    }
}

 class GameStateSerializer {

    private GameStateSerializer() {
    }

    /**
     * Serializes the specified {@link GameState} object to the output file.
     *
     * @param gameState  The game state instance to write to the file.
     * @param outputFile The file to write to.
     * @return {@code outputFile}.
     * @throws FileAlreadyExistsException if a file or directory already exists with the same path as
     *                                    {@code outputFile}.
     */
    public static Path writeTo(final GameState gameState, final Path outputFile)
            throws FileAlreadyExistsException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(outputFile);

        if (Files.exists(outputFile)) {
            throw new FileAlreadyExistsException(outputFile.toString());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            writeTo(gameState, writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    /**
     * Serializes the specified {@link GameState} object into the provided {@link BufferedWriter}.
     *
     * @param gameState The game state to serialize.
     * @param writer    The writer to write the serialized game state to.
     * @throws IOException If an I/O error occurred while writing to {@code writer}.
     * @apiNote The caller is responsible for closing {@code writer}.
     */
    static void writeTo(final GameState gameState, final BufferedWriter writer)
            throws IOException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(writer);

        writer.write(Integer.toString(gameState.getGameBoard().getNumRows()));
        writer.newLine();
        writer.write(Integer.toString(gameState.getGameBoard().getNumCols()));
        writer.newLine();
        if (gameState.hasUnlimitedLives()) {
            writer.write("");
        } else {
            writer.write(Integer.toString(gameState.getNumLives()));
        }
        writer.newLine();

        for (int r = 0; r < gameState.getGameBoard().getNumRows(); ++r) {
            final Cell[] row = gameState.getGameBoard().getRow(r);

            for (final Cell cell : row) {
                writer.write(toCellChar(cell));
            }
            writer.newLine();
        }
    }

    /**
     * Loads an input file and deserializes it into a {@link GameState} instance.
     *
     * @param inputFile The input file to read from.
     * @return An instance of {@link GameState} created from deserializing {@code inputFile}.
     * @throws FileNotFoundException if {@code inputFile} does not exist.
     */
    public static GameState loadFrom(final Path inputFile)
            throws FileNotFoundException {
        Objects.requireNonNull(inputFile);

        if (!Files.isRegularFile(inputFile)) {
            throw new FileNotFoundException(inputFile.toString());
        }

        try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            return loadFrom(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link GameState} instance by reading from the {@link BufferedReader}.
     *
     * @param reader The reader providing the serialized version of the game state.
     * @return An instance of {@link GameState} created from deserializing {@code reader}.
     * @throws IOException If an I/O error occurred while reading from {@code reader}.
     * @apiNote The caller is responsible for closing {@code reader}.
     */
    static GameState loadFrom(final BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);

        final int numRows = Integer.parseInt(reader.readLine());
        final int numCols = Integer.parseInt(reader.readLine());
        final int numLives;
        {
            final String line = reader.readLine();
            if (line.isBlank()) {
                numLives = -1;
            } else {
                numLives = Integer.parseInt(line);
            }
        }

        final Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            final String line = reader.readLine();
            for (int c = 0; c < numCols; ++c) {
                board[r][c] = fromCellChar(line.charAt(c), new Position(r, c));
            }
        }

        final GameBoard gameBoard = new GameBoard(numRows, numCols, board);
        return numLives < 0 ? new GameState(gameBoard) : new GameState(gameBoard, numLives);
    }

    /**
     * Converts an instance of {@link Cell} to its serialized character representation.
     *
     * @param cell The instance of cell to serialize.
     * @return A {@code char} representing {@code cell}.
     */
    private static char toCellChar(final Cell cell) {
        Objects.requireNonNull(cell);

        if (cell instanceof Wall) {
            return 'W';
        }

        final EntityCell cellWithEntity = (EntityCell) cell;

        // Entity takes precedence over the cell
        // We can infer the type of cell from the entity anyways
        final Entity entity = cellWithEntity.getEntity();
        if (entity instanceof ExtraLife) {
            return 'L';
        }
        if (entity instanceof Gem) {
            return 'G';
        }
        if (entity instanceof Mine) {
            return 'M';
        }
        if (entity instanceof Player) {
            return 'P';
        }

        if (cellWithEntity instanceof StopCell) {
            return 'S';
        }
        return '.';
    }

    /**
     * Converts the serialized character representation of a {@link Cell} to an instance.
     *
     * @param c        The character representing a cell.
     * @param position The position of the cell on the game board.
     * @return An instance of {@link Cell} which is represented by {@code c}.
     * @throws IllegalArgumentException if {@code c} is not a known representation of a cell.
     */
    private static Cell fromCellChar(final char c, final Position position) {
        Objects.requireNonNull(position);

        return switch (c) {
            case 'W' -> new Wall(position);
            case 'L' -> new EntityCell(position, new ExtraLife());
            case 'G' -> new EntityCell(position, new Gem());
            case 'M' -> new EntityCell(position, new Mine());
            case 'P' -> new StopCell(position, new Player());
            case 'S' -> new StopCell(position);
            case '.' -> new EntityCell(position);
            default -> throw new IllegalArgumentException("Unknown cell representation: " + c);
        };
    }
}