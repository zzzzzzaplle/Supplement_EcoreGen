import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileAlreadyExistsException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a 2D grid coordinate.
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
     * Returns a new Position offset by the specified deltas.
     *
     * @param dRow The row delta.
     * @param dCol The column delta.
     * @return A new Position instance.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Returns a new Position offset by the specified PositionOffset.
     *
     * @param offset The position offset.
     * @return A new Position instance.
     */
    public Position offsetBy(PositionOffset offset) {
        return this.offsetBy(offset.getDRow(), offset.getDCol());
    }

    /**
     * Returns a new Position offset by the specified deltas, or null if the result is out of bounds.
     *
     * @param dRow     The row delta.
     * @param dCol     The column delta.
     * @param numRows  The number of rows in the board.
     * @param numCols  The number of columns in the board.
     * @return A new Position instance within bounds, or null.
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
     * Returns a new Position offset by the specified PositionOffset, or null if the result is out of bounds.
     *
     * @param offset  The position offset.
     * @param numRows The number of rows in the board.
     * @param numCols The number of columns in the board.
     * @return A new Position instance within bounds, or null.
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
 * Represents a delta offset for a position.
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
 * Enum representing directions on the grid.
 */
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final PositionOffset offset;

    Direction(int dRow, int dCol) {
        this.offset = new PositionOffset(dRow, dCol);
    }

    /**
     * Gets the PositionOffset for this direction.
     *
     * @return The position offset.
     */
    public PositionOffset getOffset() {
        return offset;
    }

    /**
     * Gets the row offset for this direction.
     *
     * @return The row offset.
     */
    public int getRowOffset() {
        return offset.getDRow();
    }

    /**
     * Gets the column offset for this direction.
     *
     * @return The column offset.
     */
    public int getColOffset() {
        return offset.getDCol();
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
 * Abstract base class for cells on the game board.
 */
abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
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
    }

    public EntityCell(Position position) {
        super(position);
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

    public void setEntity(Entity newEntity) {
        if (this.entity != null) {
            this.entity.setOwner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setOwner(this);
        }
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
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException("StopCell can only contain a Player entity");
        }
        Entity old = getEntity();
        super.setEntity(newEntity);
        return old;
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        if (newPlayer != null) {
            setEntity(newPlayer);
        } else {
            setEntity(null);
        }
        return newPlayer;
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
 * Abstract base class for entities on the board.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public void setOwner(EntityCell ownner) {
        this.owner = ownner;
    }

    public EntityCell setowner(EntityCell ownner) {
        EntityCell prev = this.owner;
        this.owner = ownner;
        return prev;
    }
}

/**
 * A gem entity that can be collected.
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
 * An extra life entity that can be collected.
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
 * Abstract base class for move results.
 */
abstract class MoveResult {
    protected Position newPosition;

    public MoveResult() {
    }

    public MoveResult(Position newPosition) {
        this.newPosition = newPosition;
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

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}

/**
 * Result for a valid move where the player is still alive.
 */
class Valid extends MoveResult {
    protected SortedSet<Position> collectedPositions;

    public Valid() {
        super();
        this.collectedPositions = new TreeSet<>((p1, p2) -> {
            if (p1.getRow() != p2.getRow()) {
                return Integer.compare(p1.getRow(), p2.getRow());
            }
            return Integer.compare(p1.getCol(), p2.getCol());
        });
    }

    public void addCollectedPosition(Position pos) {
        if (pos != null) {
            this.collectedPositions.add(pos);
        }
    }

    public SortedSet<Position> getCollectedPositions() {
        return this.collectedPositions;
    }
}

/**
 * Result for a move where the player is alive.
 */
class Alive extends Valid {
    private SortedSet<Position> collectedGems;
    private SortedSet<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new TreeSet<>((p1, p2) -> {
            if (p1.getRow() != p2.getRow()) {
                return Integer.compare(p1.getRow(), p2.getRow());
            }
            return Integer.compare(p1.getCol(), p2.getCol());
        });
        this.collectedExtraLives = new TreeSet<>((p1, p2) -> {
            if (p1.getRow() != p2.getRow()) {
                return Integer.compare(p1.getRow(), p2.getRow());
            }
            return Integer.compare(p1.getCol(), p2.getCol());
        });
    }

    public SortedSet<Position> getCollectedGems() {
        return this.collectedGems;
    }

    public void addCollectedGem(Position pos) {
        if (pos != null) {
            this.collectedGems.add(pos);
        }
    }

    public SortedSet<Position> getCollectedExtraLives() {
        return this.collectedExtraLives;
    }

    public void addCollectedExtraLife(Position pos) {
        if (pos != null) {
            this.collectedExtraLives.add(pos);
        }
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

    public Dead(Position newPosition, Position minePosition) {
        super(newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}

/**
 * Represents the game board.
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
        this.player = null;
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
        Cell cell = board[r][c];
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        if (position == null) {
            return null;
        }
        return getEntityCell(position.getRow(), position.getCol());
    }

    public void removeGemAt(Position pos) {
        EntityCell cell = getEntityCell(pos);
        if (cell != null) {
            Entity entity = cell.getEntity();
            if (entity instanceof Gem) {
                cell.setEntity(null);
            }
        }
    }

    public void removeExtraLifeAt(Position pos) {
        EntityCell cell = getEntityCell(pos);
        if (cell != null) {
            Entity entity = cell.getEntity();
            if (entity instanceof ExtraLife) {
                cell.setEntity(null);
            }
        }
    }
    
    public void placePlayerAt(Position pos) {
        // Remove player from old position if exists
        if (player != null && player.getOwner() != null) {
            player.getOwner().setEntity(null);
        }
        
        // Place player at new position
        EntityCell cell = getEntityCell(pos);
        if (cell instanceof StopCell) {
            StopCell stopCell = (StopCell) cell;
            stopCell.setPlayer(player);
        } else if (cell != null) {
            // If it's not a StopCell, we might need to handle it, but requirements say StopCell contains Player
            // Assuming valid board state has Player on StopCell
            cell.setEntity(player);
        }
    }
    
    public Position getPlayerPosition() {
        if (player != null && player.getOwner() != null) {
            return player.getOwner().getPosition();
        }
        return null;
    }
}

/**
 * Manages the state of the game including lives, moves, and score.
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
    }

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 1;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard, this);
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
        return !hasUnlimitedLives() && numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives = numLives + delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives = numLives - delta;
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
        int boardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int undos = moveStack.getPopCount();
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undos * 2) - (numDeaths * 4);
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
    
    public int getNumLives() {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }
    
    public int getNumDeaths() {
        return numDeaths;
    }
    
    public int getNumMoves() {
        return numMoves;
    }
    
    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }
}

/**
 * Stack to manage move history for undo functionality.
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

    public void push(MoveResult move) {
        stack.push(move);
    }

    public MoveResult pop() {
        MoveResult move = stack.pop();
        popCount++;
        return move;
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

/**
 * Controller for the game board, handling movement logic.
 */
class GameBoardController {
    private GameBoard gameBoard;
    private GameState gameState;

    public GameBoardController() {
    }

    public GameBoardController(GameBoard gameBoard, GameState gameState) {
        this.gameBoard = gameBoard;
        this.gameState = gameState;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Attempts to move the player in the given direction.
     *
     * @param direction The direction to move.
     * @return The result of the move.
     */
    public MoveResult makeMove(Direction direction) {
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid();
        }

        Position startPos = gameBoard.getPlayerPosition();
        Position currentPos = startPos;
        PositionOffset offset = direction.getOffset();
        
        // Check if the first step is valid
        Position nextPos = startPos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            // Blocked by boundary
            return new Invalid();
        }
        
        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            // Blocked by wall
            return new Invalid();
        }
        
        // If it's a StopCell, we move to it and stop immediately
        if (nextCell instanceof StopCell) {
            // Move player to StopCell
            EntityCell currentEntityCell = gameBoard.getEntityCell(startPos);
            if (currentEntityCell != null) {
                currentEntityCell.setEntity(null);
            }
            StopCell stopCell = (StopCell) nextCell;
            stopCell.setPlayer(player);
            
            Alive alive = new Alive();
            alive.setNewPosition(nextPos);
            return alive;
        }
        
        // If it's an EntityCell, we need to check what entity is there
        if (nextCell instanceof EntityCell) {
            EntityCell entityCell = (EntityCell) nextCell;
            Entity entity = entityCell.getEntity();
            
            // If it's a Player, it shouldn't happen as player is at startPos
            // If it's a Gem or ExtraLife, we slide over it
            if (entity instanceof Gem || entity instanceof ExtraLife) {
                // Continue sliding
            } else if (entity instanceof Mine) {
                // Hit a mine
                Dead dead = new Dead();
                dead.setNewPosition(startPos);
                dead.setMinePosition(nextPos);
                return dead;
            }
        }
        
        // Now we slide until we hit a Wall, boundary, or StopCell
        SortedSet<Position> collectedGems = new TreeSet<>((p1, p2) -> {
            if (p1.getRow() != p2.getRow()) {
                return Integer.compare(p1.getRow(), p2.getRow());
            }
            return Integer.compare(p1.getCol(), p2.getCol());
        });
        SortedSet<Position> collectedExtraLives = new TreeSet<>((p1, p2) -> {
            if (p1.getRow() != p2.getRow()) {
                return Integer.compare(p1.getRow(), p2.getRow());
            }
            return Integer.compare(p1.getCol(), p2.getCol());
        });
        
        Position lastValidPos = startPos;
        Position pos = nextPos;
        
        while (true) {
            Cell cell = gameBoard.getCell(pos.getRow(), pos.getCol());
            
            if (cell instanceof Wall) {
                // Stop before the wall
                lastValidPos = pos.offsetBy(-offset.getDRow(), -offset.getDCol());
                break;
            }
            
            if (cell instanceof StopCell) {
                lastValidPos = pos;
                break;
            }
            
            if (cell instanceof EntityCell) {
                EntityCell entityCell = (EntityCell) cell;
                Entity entity = entityCell.getEntity();
                
                if (entity instanceof Mine) {
                    // Hit a mine
                    Dead dead = new Dead();
                    dead.setNewPosition(startPos);
                    dead.setMinePosition(pos);
                    
                    // Remove collected gems/lives that were added before the mine? 
                    // Requirements say: "any Gem or ExtraLife passed before the Mine is not collected or removed"
                    // So we just return Dead with original position
                    return dead;
                } else if (entity instanceof Gem) {
                    collectedGems.add(pos);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(pos);
                }
            }
            
            lastValidPos = pos;
            Position next = pos.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                // Hit boundary
                lastValidPos = pos;
                break;
            }
            pos = next;
        }
        
        // Move player to lastValidPos
        EntityCell currentEntityCell = gameBoard.getEntityCell(startPos);
        if (currentEntityCell != null) {
            currentEntityCell.setEntity(null);
        }
        
        EntityCell targetEntityCell = gameBoard.getEntityCell(lastValidPos);
        if (targetEntityCell instanceof StopCell) {
            ((StopCell) targetEntityCell).setPlayer(player);
        } else if (targetEntityCell != null) {
            targetEntityCell.setEntity(player);
        }
        
        // Remove collected items from board
        for (Position gemPos : collectedGems) {
            gameBoard.removeGemAt(gemPos);
        }
        for (Position lifePos : collectedExtraLives) {
            gameBoard.removeExtraLifeAt(lifePos);
        }
        
        Alive alive = new Alive();
        alive.setNewPosition(lastValidPos);
        for (Position pos : collectedGems) {
            alive.addCollectedGem(pos);
        }
        for (Position pos : collectedExtraLives) {
            alive.addCollectedExtraLife(pos);
        }
        
        return alive;
    }
    
    /**
     * Undoes the last move if possible.
     *
     * @return true if the move was undone, false otherwise.
     */
    public boolean undoMove() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        
        MoveResult lastMove = gameState.getMoveStack().pop();
        if (!(lastMove instanceof Alive)) {
            return false;
        }
        
        Alive aliveMove = (Alive) lastMove;
        Position oldPos = aliveMove.getNewPosition();
        
        // Restore gems and extra lives
        for (Position gemPos : aliveMove.getCollectedGems()) {
            EntityCell cell = gameBoard.getEntityCell(gemPos);
            if (cell != null) {
                cell.setEntity(new Gem());
            }
        }
        
        for (Position lifePos : aliveMove.getCollectedExtraLives()) {
            EntityCell cell = gameBoard.getEntityCell(lifePos);
            if (cell != null) {
                cell.setEntity(new ExtraLife());
            }
        }
        
        // Reverse extra lives effect
        if (aliveMove.getCollectedExtraLives() != null && !aliveMove.getCollectedExtraLives().isEmpty()) {
            // Decrease lives by the number of extra lives collected
            // But we need to be careful not to go below 0 if not unlimited
            if (!gameState.hasUnlimitedLives()) {
                gameState.decreaseNumLives(aliveMove.getCollectedExtraLives().size());
                if (gameState.getNumLives() < 0) {
                    gameState.setNumLives(0);
                }
            }
        }
        
        // Move player back to old position
        Player player = gameBoard.getPlayer();
        EntityCell currentEntityCell = gameBoard.getEntityCell(gameBoard.getPlayerPosition());
        if (currentEntityCell != null) {
            currentEntityCell.setEntity(null);
        }
        
        EntityCell targetEntityCell = gameBoard.getEntityCell(oldPos);
        if (targetEntityCell instanceof StopCell) {
            ((StopCell) targetEntityCell).setPlayer(player);
        } else if (targetEntityCell != null) {
            targetEntityCell.setEntity(player);
        }
        
        return true;
    }
}

/**
 * View for the game board.
 */
class GameBoardView {
    private GameBoard gameBoard;

    public GameBoardView() {
    }

    public GameBoardView(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Outputs the textual representation of the game board to {@link System#out}.
     *
     * @param useUnicodeChars If {@code true}, outputs the board elements using Unicode characters (as opposed to ASCII
     *                        characters).
     */
    public void output(boolean useUnicodeChars) {
        for (int r = 0; r < gameBoard.getNumRows(); ++r) {
            for (int c = 0; c < gameBoard.getNumCols(); ++c) {
                Cell cell = gameBoard.getCell(r, c);
                char ch = useUnicodeChars ? cell.toUnicodeChar() : cell.toASCIIChar();
                System.out.print(ch);
            }
            System.out.println();
        }
    }
}

/**
 * Main game controller.
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

    /**
     * Processes a move in the given direction.
     *
     * @param direction The direction to move.
     * @return The result of the move.
     */
    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        
        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed to the MoveStack
            return result;
        }
        
        if (result instanceof Alive) {
            // Alive moves increment numMoves, apply collected ExtraLife effects, and are pushed to the MoveStack
            gameState.incrementNumMoves();
            
            Alive aliveResult = (Alive) result;
            for (Position pos : aliveResult.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            
            gameState.getMoveStack().push(result);
            return result;
        }
        
        if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths, decrease finite lives by one, and are not pushed to the MoveStack
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            return result;
        }
        
        return result;
    }
    
    /**
     * Processes an undo request.
     *
     * @return true if the undo was successful, false otherwise.
     */
    public boolean processUndo() {
        return gameState.getGameBoardController().undoMove();
    }
}

/**
 * Serializer for game state.
 */
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

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
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

        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
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