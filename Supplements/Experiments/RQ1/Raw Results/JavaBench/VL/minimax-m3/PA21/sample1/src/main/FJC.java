import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * BoardElement interface - represents anything that can be displayed as a character.
 */
interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

/**
 * Position - represents a 2D grid coordinate.
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

    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        return new Position(this.row + offset.getDRow(), this.col + offset.getDCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

/**
 * PositionOffset - represents a delta for movement.
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
 * Direction enum - UP, DOWN, LEFT, RIGHT.
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
 * Abstract Cell class.
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

    @Override
    public char toUnicodeChar() {
        return '.';
    }

    @Override
    public char toASCIIChar() {
        return '.';
    }
}

/**
 * Wall cell.
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
 * Abstract Entity class.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
        this.owner = null;
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setowner(EntityCell ownner) {
        this.owner = ownner;
        return ownner;
    }

    @Override
    public char toUnicodeChar() {
        return '?';
    }

    @Override
    public char toASCIIChar() {
        return '?';
    }
}

/**
 * EntityCell class.
 */
class EntityCell extends Cell {
    protected Entity entity;

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
            entity.setowner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setentity(Entity newEntity) {
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return newEntity;
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
 * StopCell class - only contains a Player entity.
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
    public Entity setentity(Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException("StopCell can only contain a Player entity");
        }
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        setentity(newPlayer);
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
 * ExtraLife entity.
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
 * Gem entity.
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
 * Mine entity.
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
 * Player entity.
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
 * MoveResult abstract class.
 */
abstract class MoveResult {
    public Position newPosition;

    public MoveResult() {
        this.newPosition = new Position();
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
 * Valid move result.
 */
class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
        super();
        this.origPosition = new Position();
    }

    public Valid(Position origPosition, Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}

/**
 * Alive move result.
 */
class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position origPosition, Position newPosition, List<Position> collectedGems, List<Position> collectedExtraLives) {
        super(origPosition, newPosition);
        this.collectedGems = collectedGems;
        this.collectedExtraLives = collectedExtraLives;
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
 * Dead move result.
 */
class Dead extends Valid {
    public Position minePosition;

    public Dead() {
        super();
        this.minePosition = new Position();
    }

    public Dead(Position origPosition, Position minePosition) {
        super(origPosition, origPosition);
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
 * Invalid move result.
 */
class Invalid extends MoveResult {
    public Invalid() {
        super();
    }

    public Invalid(Position position) {
        super(position);
    }
}

/**
 * MoveStack class.
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
        if (stack.isEmpty()) return null;
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
 * GameBoard class.
 */
class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    public Player player;

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
        // Find player
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] instanceof EntityCell) {
                    Entity e = ((EntityCell) board[r][c]).getEntity();
                    if (e instanceof Player) {
                        this.player = (Player) e;
                        return;
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
        return board[r];
    }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int i = 0; i < numRows; i++) {
            col[i] = board[i][c];
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
                if (board[r][c] instanceof EntityCell) {
                    Entity e = ((EntityCell) board[r][c]).getEntity();
                    if (e instanceof Gem) {
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
        return getEntityCell(position.getRow(), position.getCol());
    }
}

/**
 * GameBoardController class.
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

    public MoveResult makeMove(Direction direction) {
        PositionOffset offset = direction.getOffset();
        Position playerPos = gameBoard.player.getOwner().getPosition();
        Position origPos = new Position(playerPos.getRow(), playerPos.getCol());

        // Check first step
        Position nextPos = playerPos.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
        if (nextPos == null) {
            return new Invalid(origPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(origPos);
        }
        if (nextCell instanceof StopCell) {
            // StopCell - player enters and stops
            EntityCell currentEC = (EntityCell) gameBoard.getCell(playerPos.getRow(), playerPos.getCol());
            EntityCell targetEC = (EntityCell) nextCell;
            // Move player
            currentEC.setentity(null);
            targetEC.setentity(gameBoard.player);
            return new Alive(origPos, nextPos, new ArrayList<>(), new ArrayList<>());
        }

        // Slide
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position currentPos = playerPos;
        Position lastValidPos = playerPos;

        while (nextPos != null) {
            Cell cell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
            if (cell instanceof Wall) {
                break;
            }
            if (cell instanceof StopCell) {
                break;
            }
            EntityCell ec = (EntityCell) cell;
            Entity entity = ec.getEntity();
            if (entity instanceof Mine) {
                // Dead - board unchanged, position is original
                return new Dead(origPos, nextPos);
            }
            if (entity instanceof Gem) {
                collectedGems.add(new Position(nextPos.getRow(), nextPos.getCol()));
            }
            if (entity instanceof ExtraLife) {
                collectedExtraLives.add(new Position(nextPos.getRow(), nextPos.getCol()));
            }
            lastValidPos = nextPos;
            currentPos = nextPos;
            nextPos = currentPos.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
        }

        // Apply movement: remove player from old position, add to new position
        EntityCell oldEC = (EntityCell) gameBoard.getCell(playerPos.getRow(), playerPos.getCol());
        oldEC.setentity(null);

        // Remove collected gems and extra lives
        for (Position p : collectedGems) {
            EntityCell ec = gameBoard.getEntityCell(p);
            ec.setentity(null);
        }
        for (Position p : collectedExtraLives) {
            EntityCell ec = gameBoard.getEntityCell(p);
            ec.setentity(null);
        }

        // Place player at lastValidPos
        EntityCell newEC = (EntityCell) gameBoard.getCell(lastValidPos.getRow(), lastValidPos.getCol());
        newEC.setentity(gameBoard.player);

        return new Alive(origPos, lastValidPos, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        // Restore player to origPosition
        Position origPos = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        // Remove player from newPos
        EntityCell newEC = (EntityCell) gameBoard.getCell(newPos.getRow(), newPos.getCol());
        newEC.setentity(null);

        // Place player at origPos
        EntityCell origEC = (EntityCell) gameBoard.getCell(origPos.getRow(), origPos.getCol());
        origEC.setentity(gameBoard.player);

        // Restore gems
        for (Position p : alive.getCollectedGems()) {
            EntityCell ec = gameBoard.getEntityCell(p);
            ec.setentity(new Gem());
        }
        // Restore extra lives
        for (Position p : alive.getCollectedExtraLives()) {
            EntityCell ec = gameBoard.getEntityCell(p);
            ec.setentity(new ExtraLife());
        }
    }
}

/**
 * GameBoardView class.
 */
class GameBoardView {
    private GameBoard gameBoard;

    public GameBoardView() {
        this.gameBoard = new GameBoard();
    }

    /**
     * Creates a view of the provided game board.
     *
     * @param gameBoard Game board instance to create a view from.
     */
    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
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
 * GameState class.
 */
class GameState {
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
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
        this.gameBoard = new GameBoard();
        this.initialNumOfGems = 0;
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = UNLIMITED_LIVES;
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.gameBoard = gameBoard;
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
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
        if (hasUnlimitedLives()) return false;
        return numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) return Integer.MAX_VALUE;
        this.numLives += delta;
        return this.numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) return Integer.MAX_VALUE;
        this.numLives -= delta;
        return this.numLives;
    }

    public int decrementNumLives() {
        if (hasUnlimitedLives()) return Integer.MAX_VALUE;
        this.numLives--;
        return this.numLives;
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
        int undoes = moveStack.getPopCount();
        return initialNumOfGems + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
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
}

/**
 * GameController class.
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
        gameState.incrementNumMoves();
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.increaseNumLives(alive.getCollectedExtraLives().size());
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
        }
        return result;
    }

    public boolean processUndo() {
        MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }
        MoveResult move = stack.pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            gameState.getGameBoardController().undoMove(alive);
            if (!gameState.hasUnlimitedLives()) {
                gameState.decreaseNumLives(alive.getCollectedExtraLives().size());
            }
            return true;
        }
        return false;
    }
}

/**
 * GameStateSerializer - utility for loading and saving game state.
 */
class GameStateSerializer {

    private GameStateSerializer() {
    }

    /**
     * Serializes the specified {@link GameState} object to the output file.
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
     */
    private static char toCellChar(final Cell cell) {
        Objects.requireNonNull(cell);

        if (cell instanceof Wall) {
            return 'W';
        }

        final EntityCell cellWithEntity = (EntityCell) cell;

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