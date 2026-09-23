import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Represents a 2D coordinate on the game board.
 */
class Position {
    private int row;
    private int col;

    public Position() {
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
     * Returns a new Position offset by the given row and column deltas.
     *
     * @param dRow The delta for the row.
     * @param dCol The delta for the column.
     * @return A new Position.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Returns a new Position offset by the given PositionOffset.
     *
     * @param offset The offset to apply.
     * @return A new Position.
     */
    public Position offsetBy(PositionOffset offset) {
        return this.offsetBy(offset.getDRow(), offset.getDCol());
    }

    /**
     * Returns a new Position offset by the given deltas if within bounds, otherwise null.
     *
     * @param dRow     The delta for the row.
     * @param dCol     The delta for the column.
     * @param numRows  The total number of rows.
     * @param numCols  The total number of columns.
     * @return A new Position or null if out of bounds.
     */
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;

        if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
            return new Position(newRow, newCol);
        }
        return null;
    }

    /**
     * Returns a new Position offset by the given offset if within bounds, otherwise null.
     *
     * @param offset   The offset to apply.
     * @param numRows  The total number of rows.
     * @param numCols  The total number of columns.
     * @return A new Position or null if out of bounds.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return this.offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

/**
 * Represents a delta offset (row and column change).
 */
class PositionOffset {
    private int dRow;
    private int dCol;

    public PositionOffset() {
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
 * Enum representing the four cardinal directions.
 */
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dRow;
    private final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public PositionOffset getOffset() {
        return new PositionOffset(dRow, dCol);
    }

    public int getRowOffset() {
        return dRow;
    }

    public int getColOffset() {
        return dCol;
    }
}

/**
 * Interface for elements that can be rendered as characters.
 */
interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

/**
 * Abstract base class for cells in the game board.
 */
abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}

/**
 * A wall cell. Cannot be entered.
 */
class Wall extends Cell {
    public Wall() {
        super();
    }

    public Wall(Position position) {
        super();
        this.position = position;
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
 * A cell that can contain an entity (Player, Gem, Mine, ExtraLife).
 */
class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {
        super();
    }

    public EntityCell(Position position) {
        super();
        this.position = position;
        this.entity = null;
    }

    public EntityCell(Position position, Entity entity) {
        super();
        this.position = position;
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
 * A stop cell. The player stops here. Can contain a Player.
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
        Entity old = getEntity();
        setEntity(newPlayer);
        return (Player) old;
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
 * Abstract base class for entities on the board.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
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
 * A player entity.
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
 * A gem entity. Collectible.
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
 * A mine entity. Causes death if touched.
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
 * An extra life entity. Collectible.
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
 * Base class for move results.
 */
abstract class MoveResult {
    protected Position newPosition;

    public MoveResult() {
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }
}

/**
 * Result for an invalid move (blocked immediately).
 */
class Invalid extends MoveResult {
}

/**
 * Base class for valid moves.
 */
class Valid extends MoveResult {
    protected Position origPosition;

    public Valid() {
        super();
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}

/**
 * Result for a successful move where the player is alive.
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
 * Result for a move where the player hit a mine and died.
 */
class Dead extends Valid {
    private Position minePosition;

    public Dead() {
        super();
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}

/**
 * The game board structure.
 */
class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;
    private Player player;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        // Find player
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        this.player = (Player) e;
                        break;
                    }
                }
            }
            if (player != null) break;
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
        return board[r][c];
    }

    public EntityCell getEntityCell(int r, int c) {
        Cell cell = board[r][c];
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }

    /**
     * Counts the number of Gems currently on the board.
     */
    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}

/**
 * Stack to manage undoable moves.
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
        stack.push(move);
    }

    public MoveResult pop() {
        MoveResult result = stack.pop();
        popCount++;
        return result;
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

/**
 * Controller for game board logic (movement, collision).
 */
class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
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
     * Executes a move in the given direction.
     */
    public MoveResult makeMove(Direction direction) {
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        // Find player's current position
        Position currentPos = null;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e == player) {
                        currentPos = cell.getPosition();
                        break;
                    }
                }
            }
            if (currentPos != null) break;
        }

        if (currentPos == null) {
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        // Check first step
        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid();
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());

        // If first cell is Wall or Boundary (already handled by offsetByOrNull), it's invalid
        if (nextCell instanceof Wall) {
            return new Invalid();
        }

        // Slide logic
        Position finalPos = currentPos;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean hitMine = false;
        Position minePos = null;

        Position walkPos = nextPos;

        while (true) {
            Cell cellAtWalk = gameBoard.getCell(walkPos.getRow(), walkPos.getCol());

            if (cellAtWalk instanceof Wall) {
                // Stop before wall. Final pos is previous cell.
                break;
            } else if (cellAtWalk instanceof StopCell) {
                // Stop ON stop cell.
                finalPos = walkPos;
                break;
            } else if (cellAtWalk instanceof EntityCell) {
                EntityCell ec = (EntityCell) cellAtWalk;
                Entity e = ec.getEntity();
                if (e instanceof Mine) {
                    hitMine = true;
                    minePos = walkPos;
                    break;
                } else if (e instanceof Gem) {
                    // Collect gem
                    collectedGems.add(walkPos);
                    // Remove gem from board
                    ec.setEntity(null);
                } else if (e instanceof ExtraLife) {
                    // Collect extra life
                    collectedExtraLives.add(walkPos);
                    // Remove extra life from board
                    ec.setEntity(null);
                } else if (e instanceof Player) {
                    // Should not happen as player is moving away, but ignore
                }
                // Continue sliding
                finalPos = walkPos;
            } else {
                // Empty EntityCell or just empty space
                finalPos = walkPos;
            }

            // Move to next
            Position nextWalk = walkPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (nextWalk == null) {
                // Hit boundary
                break;
            }
            walkPos = nextWalk;
        }

        // If hit mine, do not move player, do not collect gems passed before mine
        if (hitMine) {
            Dead deadResult = new Dead();
            deadResult.setOrigPosition(currentPos);
            deadResult.setNewPosition(currentPos); // Position remains original
            deadResult.setMinePosition(minePos);
            return deadResult;
        }

        // If no movement occurred (e.g. blocked by wall immediately after start check? No, start check was Wall. 
        // If blocked by boundary immediately? Handled by nextPos check.
        // If blocked by wall at nextPos? Handled.
        // If blocked by stop cell at nextPos? finalPos becomes nextPos. Movement occurred.
        // If nextPos was valid but loop didn't run? 
        // Wait, if nextPos is valid, we entered the loop.
        // If we stopped immediately because nextPos was a Wall? No, nextPos Wall returns Invalid.
        // What if nextPos is the only step and it's a boundary? nextPos is null -> Invalid.
        
        // Check if moved
        if (finalPos.equals(currentPos)) {
            return new Invalid();
        }

        // Move player to finalPos
        // Remove player from old position
        EntityCell oldEC = gameBoard.getEntityCell(currentPos);
        if (oldEC != null) {
            oldEC.setEntity(null);
        }

        // Place player at finalPos
        EntityCell newEC = gameBoard.getEntityCell(finalPos);
        if (newEC != null) {
            newEC.setEntity(player);
        } else {
            // Should not happen based on logic, but safety
            // If finalPos was a non-entity cell (empty EntityCell or StopCell without entity), it's fine.
            // If it was a Wall, we wouldn't be here.
        }

        Alive aliveResult = new Alive();
        aliveResult.setOrigPosition(currentPos);
        aliveResult.setNewPosition(finalPos);
        aliveResult.setCollectedGems(collectedGems);
        aliveResult.setCollectedExtraLives(collectedExtraLives);

        return aliveResult;
    }
}

/**
 * View for the game board.
 */
class GameBoardView {
    private final GameBoard gameBoard;

    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

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
 * Manages game state, lives, moves, score.
 */
class GameState {
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
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 3; // Default? Or require constructor. Let's assume default 3 for no-arg, but constructor below handles init.
        this.initialNumOfGems = 0;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 3; // Default finite lives
        this.initialNumOfGems = gameBoard.getNumGems();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        if (this.gameBoardController != null) {
            this.gameBoardController.setGameBoard(gameBoard);
        }
        if (this.gameBoardView != null) {
            this.gameBoardView = new GameBoardView(gameBoard);
        }
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
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return numLives == 0 && !hasUnlimitedLives();
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return numLives;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return numLives;
        }
        numLives -= delta;
        return numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
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
        int undos = moveStack.getPopCount();
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        
        int score = initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undos * 2) - (numDeaths * 4);
        return score;
    }
}

/**
 * Main game controller handling logic and state transitions.
 */
class GameController {
    private GameState gameState;

    public GameController() {
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
        if (gameState.hasLost()) {
            return new Invalid();
        }

        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            
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
            // Dead moves are NOT pushed to stack
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        MoveResult lastMove = gameState.getMoveStack().pop();
        
        if (lastMove instanceof Alive) {
            Alive alive = (Alive) lastMove;
            
            // Restore player position
            Player player = gameState.getGameBoard().getPlayer();
            if (player != null) {
                // Remove player from current position (which is the end of the move)
                Position currentPos = player.getOwner().getPosition();
                gameState.getGameBoard().getEntityCell(currentPos).setEntity(null);
                
                // Place player at original position
                Position origPos = alive.getOrigPosition();
                EntityCell origEC = gameState.getGameBoard().getEntityCell(origPos);
                origEC.setEntity(player);
            }

            // Restore collected Gems to the board
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell ec = gameState.getGameBoard().getEntityCell(gemPos);
                if (ec.getEntity() == null) {
                    ec.setEntity(new Gem());
                }
            }

            // Reverse ExtraLife gains
            for (Position lifePos : alive.getCollectedExtraLives()) {
                gameState.decreaseNumLives(1);
                // Also remove the ExtraLife from the board
                EntityCell ec = gameState.getGameBoard().getEntityCell(lifePos);
                if (ec.getEntity() != null) {
                     // It shouldn't be there, but just in case
                     ec.setEntity(null);
                }
            }
            
            return true;
        }
        
        return false;
    }
}