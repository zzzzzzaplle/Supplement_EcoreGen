import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

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
     * Creates a new position offset by the given row and column deltas.
     *
     * @param dRow The vertical offset.
     * @param dCol The horizontal offset.
     * @return A new Position instance.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Creates a new position offset by the given PositionOffset.
     *
     * @param offset The offset deltas.
     * @return A new Position instance.
     */
    public Position offsetBy(PositionOffset offset) {
        return offsetBy(offset.getDRow(), offset.getDCol());
    }

    /**
     * Creates a new position offset by the given row and column deltas, checking boundaries.
     *
     * @param dRow     The vertical offset.
     * @param dCol     The horizontal offset.
     * @param numRows  Total number of rows in the board.
     * @param numCols  Total number of columns in the board.
     * @return A new Position if within bounds, null otherwise.
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
     * Creates a new position offset by the given PositionOffset, checking boundaries.
     *
     * @param offset   The offset deltas.
     * @param numRows  Total number of rows in the board.
     * @param numCols  Total number of columns in the board.
     * @return A new Position if within bounds, null otherwise.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }
}

/**
 * Represents a row/column offset (delta).
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
 * Enumeration of movement directions.
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
 * Base interface for elements that can be displayed on the board.
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
 * A cell that can contain an Entity (Player, Gem, Mine, ExtraLife).
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

    public Entity setEntity(Entity newEntity) {
        Entity oldEntity = this.entity;
        if (this.entity != null) {
            this.entity.setOwner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setOwner(this);
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
 * A special EntityCell that can only contain a Player.
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
 * Represents a Wall cell.
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
 * Base abstract class for entities on the board.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell ownner) {
        this.owner = ownner;
        return this.owner;
    }

    public EntityCell setowner(EntityCell ownner) {
        EntityCell prev = this.owner;
        this.owner = ownner;
        return prev;
    }
}

/**
 * Represents a Player entity.
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
 * Represents a Gem entity.
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
 * Represents a Mine entity.
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
 * Represents an ExtraLife entity.
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
 * Result for an invalid move (no movement occurred).
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
 * Base class for valid moves.
 */
abstract class Valid extends MoveResult {
    protected Position origPosition;

    public Valid() {
        super();
    }

    public Valid(Position newPosition, Position origPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }
}

/**
 * Result for a move where the player survived.
 */
class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position newPosition, Position origPosition) {
        super(newPosition, origPosition);
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
 * Result for a move where the player hit a mine.
 */
class Dead extends Valid {
    private Position minePosition;

    public Dead() {
        super();
    }

    public Dead(Position newPosition, Position origPosition, Position minePosition) {
        super(newPosition, origPosition);
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
 * Manages the 2D grid of cells.
 */
class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
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

    public Cell[] getRow(int r) {
        if (r < 0 || r >= numRows) {
            return new Cell[0];
        }
        return board[r];
    }

    public Cell[] getCol(int c) {
        if (c < 0 || c >= numCols) {
            return new Cell[0];
        }
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
        if (position == null) {
            return null;
        }
        return getEntityCell(position.getRow(), position.getCol());
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
        this.stack = new ArrayDeque<>();
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
        popCount++;
        return stack.pop();
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

/**
 * Controls the logic of moving the player on the board.
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
     * Executes a move in the specified direction.
     *
     * @param direction The direction to move.
     * @return The result of the move.
     */
    public MoveResult makeMove(Direction direction) {
        GameBoard board = gameBoard;
        int numRows = board.getNumRows();
        int numCols = board.getNumCols();

        // Find player position
        Position playerPos = null;
        EntityCell playerCell = null;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        playerPos = cell.getPosition();
                        playerCell = (EntityCell) cell;
                        break;
                    }
                }
            }
            if (playerPos != null) break;
        }

        if (playerPos == null) {
            // Should not happen in a valid game state
            return new Invalid();
        }

        PositionOffset offset = direction.getOffset();
        Position currentPos = playerPos;
        Position nextPos = currentPos.offsetByOrNull(offset, numRows, numCols);

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();

        // If the first step is invalid (wall or boundary), it's an Invalid move
        if (nextPos == null) {
            return new Invalid(playerPos);
        }

        Cell nextCell = board.getCell(nextPos.getRow(), nextPos.getCol());

        // Check if first cell is a wall
        if (nextCell instanceof Wall) {
            return new Invalid(playerPos);
        }

        // Check if first cell is a mine
        if (nextCell instanceof EntityCell) {
            Entity entity = ((EntityCell) nextCell).getEntity();
            if (entity instanceof Mine) {
                // Dead move, position remains original
                return new Dead(playerPos, playerPos, nextPos);
            }
        }

        // Start sliding
        Position lastValidPos = currentPos;
        Position finalPos = currentPos;
        boolean hitMine = false;
        Position minePos = null;

        // We need to track if we moved at least one step.
        // The loop continues until we hit a wall, boundary, or stop cell.
        
        boolean moved = false;
        Position tempPos = nextPos;
        
        while (true) {
            moved = true;
            lastValidPos = tempPos;
            Cell cell = board.getCell(tempPos.getRow(), tempPos.getCol());

            if (cell instanceof Wall) {
                // Stop before wall. Final position is lastValidPos.
                break;
            } else if (cell instanceof EntityCell) {
                Entity entity = ((EntityCell) cell).getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = tempPos;
                    break;
                } else if (entity instanceof Gem) {
                    collectedGems.add(tempPos);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(tempPos);
                } else if (entity instanceof Player) {
                    // Player shouldn't be here, but handle gracefully
                    break;
                }
            } else if (cell instanceof StopCell) {
                // Stop exactly on StopCell.
                break;
            }

            // Calculate next step
            Position potentialNext = tempPos.offsetByOrNull(offset, numRows, numCols);
            if (potentialNext == null) {
                // Hit boundary. Final position is lastValidPos.
                break;
            }
            
            // Check if next cell is wall immediately
            Cell nextPotentialCell = board.getCell(potentialNext.getRow(), potentialNext.getCol());
            if (nextPotentialCell instanceof Wall) {
                break;
            }
            
            tempPos = potentialNext;
        }

        if (hitMine) {
            // Dead move: position remains original, board unchanged
            return new Dead(playerPos, playerPos, minePos);
        } else {
            // Alive move
            Alive result = new Alive(lastValidPos, playerPos);
            result.setCollectedGems(collectedGems);
            result.setCollectedExtraLives(collectedExtraLives);
            
            // Update board: remove collected items, move player
            // Remove collected gems and extra lives
            for (Position pos : collectedGems) {
                EntityCell eCell = board.getEntityCell(pos);
                if (eCell != null) {
                    eCell.setEntity(null);
                }
            }
            for (Position pos : collectedExtraLives) {
                EntityCell eCell = board.getEntityCell(pos);
                if (eCell != null) {
                    eCell.setEntity(null);
                }
            }

            // Move player
            // Remove player from old position
            playerCell.setEntity(null);
            
            // Place player at new position
            EntityCell newPlayerCell = board.getEntityCell(lastValidPos);
            if (newPlayerCell != null) {
                // If it's a StopCell, it's fine. If it's EntityCell, it's fine.
                // If it was a StopCell with null entity, it still works.
                newPlayerCell.setEntity(new Player());
            } else {
                // Should not happen if logic is correct
            }

            return result;
        }
    }
}

/**
 * Manages the game state, including lives, moves, and access to controllers/views.
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
        this.numLives = UNLIMITED_LIVES;
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
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && getNumLives() <= 0;
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
        int collectedGems = initialNumOfGems - getNumGems();
        int moves = numMoves;
        int undos = moveStack.getPopCount();
        int deaths = numDeaths;
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        
        return initialBoardSize + (collectedGems * 10) - (moves * 1) - (undos * 2) - (deaths * 4);
    }
}

/**
 * Handles the main game loop, user input, and serialization.
 */
 class InertiaTextGame {
    public static void main(String[] args) {
        // Default to a simple board if no file provided, or load from file if provided
        GameState gameState = null;
        
        if (args.length > 0 && args[0].endsWith(".txt")) {
            try {
                gameState = GameStateSerializer.loadFrom(java.nio.file.Paths.get(args[0]));
            } catch (java.io.FileNotFoundException e) {
                System.err.println("File not found: " + args[0]);
                return;
            } catch (Exception e) {
                System.err.println("Error loading game state: " + e.getMessage());
                return;
            }
        } else {
            // Create a default board for demonstration
            gameState = createDefaultGame();
        }

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (!gameState.hasWon() && !gameState.hasLost()) {
            System.out.println("Current Score: " + gameState.getScore());
            System.out.println("Lives: " + gameState.getNumLives());
            System.out.println("Gems Remaining: " + gameState.getNumGems());
            
            gameState.getGameBoardView().output(true); // Use Unicode
            
            System.out.print("Enter move (U/D/L/R), Undo, or Quit: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("QUIT")) {
                break;
            } else if (input.equals("UNDO")) {
                if (gameState.getGameBoardController() != null) {
                    // Note: processUndo is in GameController, but we are using GameState directly.
                    // We need a GameController instance or handle undo in GameState.
                    // The requirements say GameController.processUndo.
                    // Let's assume we need to create a GameController or add method to GameState.
                    // For this implementation, we'll add a helper method or use the logic directly.
                    // To stick to the design, let's assume we can access a GameController or add the method to GameState.
                    // Actually, the design has GameController wrapping GameState.
                    // Let's instantiate a GameController for the loop if not present, or just call the logic.
                    // Re-reading requirements: "GameState ... creates/returns GameBoardController".
                    // It doesn't explicitly say GameState creates GameController.
                    // However, GameController has a gameState field.
                    // Let's create a GameController instance for the main loop.
                }
                
                // Since we don't have a GameController instance in GameState, let's create one or add the logic.
                // To adhere strictly to the provided snippets and design, 
                // I will assume the main loop uses a GameController if available, or we add the method to GameState.
                // Given the constraints, I will add a processUndo method to GameState that mimics the behavior,
                // OR I will instantiate a GameController here.
                // Let's instantiate a GameController.
                GameController controller = new GameController(gameState);
                boolean success = controller.processUndo();
                if (success) {
                    System.out.println("Move undone.");
                } else {
                    System.out.println("Nothing to undo.");
                }
            } else if (input.equals("U") || input.equals("D") || input.equals("L") || input.equals("R")) {
                Direction dir = null;
                if (input.equals("U")) dir = Direction.UP;
                else if (input.equals("D")) dir = Direction.DOWN;
                else if (input.equals("L")) dir = Direction.LEFT;
                else if (input.equals("R")) dir = Direction.RIGHT;
                
                if (dir != null) {
                    GameController controller = new GameController(gameState);
                    MoveResult result = controller.processMove(dir);
                    
                    if (result instanceof Invalid) {
                        System.out.println("Invalid move.");
                    } else if (result instanceof Dead) {
                        System.out.println("You hit a mine and died!");
                        gameState.incrementNumDeaths();
                        gameState.decrementNumLives();
                    } else if (result instanceof Alive) {
                        Alive aliveResult = (Alive) result;
                        gameState.incrementNumMoves();
                        
                        // Apply collected extra lives
                        for (Position pos : aliveResult.getCollectedExtraLives()) {
                            gameState.increaseNumLives(1);
                        }
                    }
                }
            } else {
                System.out.println("Unknown command.");
            }
        }

        if (gameState.hasWon()) {
            System.out.println("Congratulations! You won!");
            System.out.println("Final Score: " + gameState.getScore());
        } else if (gameState.hasLost()) {
            System.out.println("Game Over! You lost.");
        }
        
        scanner.close();
    }

    private static GameState createDefaultGame() {
        int numRows = 5;
        int numCols = 5;
        Cell[][] board = new Cell[numRows][numCols];

        // Initialize with walls and empty cells
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (r == 0 || r == numRows - 1 || c == 0 || c == numCols - 1) {
                    board[r][c] = new Wall(new Position(r, c));
                } else {
                    board[r][c] = new EntityCell(new Position(r, c));
                }
            }
        }

        // Place player
        board[1][1] = new StopCell(new Position(1, 1), new Player());

        // Place gems
        board[1][3] = new EntityCell(new Position(1, 3), new Gem());
        board[3][3] = new EntityCell(new Position(3, 3), new Gem());
        board[3][1] = new EntityCell(new Position(3, 1), new Gem());

        // Place mine
        board[2][2] = new EntityCell(new Position(2, 2), new Mine());
        
        // Place stop cell
        board[2][4] = new StopCell(new Position(2, 4));

        return new GameState(new GameBoard(numRows, numCols, board), 3);
    }
}