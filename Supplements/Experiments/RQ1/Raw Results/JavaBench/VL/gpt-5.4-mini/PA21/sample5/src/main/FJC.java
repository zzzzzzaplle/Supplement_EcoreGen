import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileAlreadyExistsException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Core board element contract.
 */
interface BoardElement {
    char toUnicodeChar();

    char toASCIIChar();
}

/**
 * A board cell with a fixed position.
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
 * Directions for player movement.
 */
enum Direction {
    UP(new PositionOffset(-1, 0)),
    DOWN(new PositionOffset(1, 0)),
    LEFT(new PositionOffset(0, -1)),
    RIGHT(new PositionOffset(0, 1));

    private PositionOffset offset;

    Direction(PositionOffset offset) {
        this.offset = offset;
    }

    Direction() {
    }

    public PositionOffset getOffset() {
        return offset;
    }

    public int getRowOffset() {
        return offset == null ? 0 : offset.getDRow();
    }

    public int getColOffset() {
        return offset == null ? 0 : offset.getDCol();
    }

    public void setOffset(PositionOffset offset) {
        this.offset = offset;
    }
}

/**
 * Base entity type.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }

    public EntityCell setowner(EntityCell ownner) {
        setOwner(ownner);
        return ownner;
    }
}

/**
 * A cell capable of holding at most one entity.
 */
class EntityCell extends Cell {
    Entity entity;

    public EntityCell() {
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        setEntity(entity);
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setentity(Entity newEntity) {
        return setEntity(newEntity);
    }

    public Entity setEntity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
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
 * Extra life entity.
 */
class ExtraLife extends Entity {
    public ExtraLife() {
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
 * A wall cell.
 */
class Wall extends Cell {
    public Wall() {
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
 * A stop cell.
 */
class StopCell extends EntityCell {
    public StopCell() {
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    public Entity setentity(Entity newEntity) {
        return setEntity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        setEntity(newPlayer);
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
 * A position on the board.
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

    public Position offsetBy(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        return offsetBy(offset.getDRow(), offset.getDCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        Position p = offsetBy(dRow, dCol);
        if (p.row < 0 || p.row >= numRows || p.col < 0 || p.col >= numCols) {
            return null;
        }
        return p;
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }
}

/**
 * A 2D offset.
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
 * The game board.
 */
class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    public Player player;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = findPlayer();
    }

    public GameBoard(int numRows, int numCols, Cell[][] board, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = player;
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
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        return (EntityCell) board[r][c];
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }

    public Player findPlayer() {
        if (board == null) {
            return player;
        }
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        return (Player) e;
                    }
                }
            }
        }
        return player;
    }
}

/**
 * Move result base type.
 */
abstract class MoveResult {
    public Position newPosition;

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
 * A valid move result.
 */
class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
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
    public List<Position> collectedGems = new ArrayList<>();
    public List<Position> collectedExtraLives = new ArrayList<>();

    public Alive() {
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
    }
}

/**
 * A stack of move results.
 */
class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new ArrayDeque<>();

    public MoveStack() {
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
        MoveResult m = stack.pop();
        popCount++;
        return m;
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

/**
 * The game state.
 */
class GameState {
    public static int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this(gameBoard, -1);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(this.gameBoard);
        this.gameBoardView = new GameBoardView(this.gameBoard);
        this.initialNumOfGems = gameBoard == null ? 0 : gameBoard.getNumGems();
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
        if (!hasUnlimitedLives()) {
            numLives += delta;
        }
        return getNumLives();
    }

    public int decreaseNumLives(int delta) {
        if (!hasUnlimitedLives()) {
            numLives -= delta;
        }
        return getNumLives();
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        return ++numMoves;
    }

    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    public int getNumGems() {
        return gameBoard == null ? 0 : gameBoard.getNumGems();
    }

    public int getScore() {
        int initialBoardSize = gameBoard == null ? 0 : gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - getNumGems();
        return initialBoardSize + (collectedGems * 10) - numMoves - (moveStack == null ? 0 : moveStack.getPopCount() * 2) - (numDeaths * 4);
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
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
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

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
}

/**
 * Controller for board movement.
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

    public MoveResult makeMove(Direction direction) {
        if (gameBoard == null || gameBoard.getPlayer() == null) {
            return new Invalid();
        }
        Position start = findPlayerPosition();
        if (start == null) {
            return new Invalid();
        }
        PositionOffset offset = direction.getOffset();
        Position current = start;
        List<Position> gems = new ArrayList<>();
        List<Position> lives = new ArrayList<>();
        boolean moved = false;

        while (true) {
            Position next = current.offsetByOrNull(offset, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                return moved ? finishAlive(start, current, gems, lives) : new Invalid();
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                return moved ? finishAlive(start, current, gems, lives) : new Invalid();
            }
            moved = true;
            if (nextCell instanceof StopCell) {
                movePlayer(start, next);
                return finishAlive(start, next, gems, lives);
            }
            if (nextCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) nextCell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    return finishDead(start, next);
                }
                if (entity instanceof Gem) {
                    gems.add(next);
                    ec.setEntity(null);
                } else if (entity instanceof ExtraLife) {
                    lives.add(next);
                    ec.setEntity(null);
                }
            }
            current = next;
        }
    }

    private MoveResult finishAlive(Position start, Position end, List<Position> gems, List<Position> lives) {
        movePlayer(start, end);
        Alive alive = new Alive();
        alive.setOrigPosition(start);
        alive.setNewPosition(end);
        alive.setCollectedGems(gems);
        alive.setCollectedExtraLives(lives);
        return alive;
    }

    private MoveResult finishDead(Position start, Position minePosition) {
        Dead dead = new Dead();
        dead.setOrigPosition(start);
        dead.setNewPosition(start);
        dead.setMinePosition(minePosition);
        return dead;
    }

    private void movePlayer(Position start, Position end) {
        EntityCell startCell = gameBoard.getEntityCell(start);
        EntityCell endCell = gameBoard.getEntityCell(end);
        Player player = gameBoard.getPlayer();
        if (startCell != null) {
            startCell.setEntity(null);
        }
        if (endCell instanceof StopCell) {
            ((StopCell) endCell).setPlayer(player);
        } else {
            endCell.setEntity(player);
        }
        gameBoard.setPlayer(player);
    }

    private Position findPlayerPosition() {
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        return new Position(r, c);
                    }
                }
            }
        }
        return null;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Position orig = alive.getOrigPosition();
        Position cur = alive.getNewPosition();
        EntityCell origCell = gameBoard.getEntityCell(orig);
        EntityCell curCell = gameBoard.getEntityCell(cur);
        Player player = gameBoard.getPlayer();
        if (curCell != null) {
            curCell.setEntity(null);
        }
        if (origCell instanceof StopCell) {
            ((StopCell) origCell).setPlayer(player);
        } else {
            origCell.setEntity(player);
        }
        for (Position p : alive.getCollectedGems()) {
            gameBoard.getEntityCell(p).setEntity(new Gem());
        }
        for (Position p : alive.getCollectedExtraLives()) {
            gameBoard.getEntityCell(p).setEntity(new ExtraLife());
        }
    }
}

/**
 * Game controller.
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
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        if (result instanceof Invalid) {
            return result;
        }
        gameState.incrementNumMoves();
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult move = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(move);
        Alive alive = (Alive) move;
        for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
            gameState.decreaseNumLives(1);
        }
        return true;
    }
}

/**
 * Read-only board output view.
 */
class GameBoardView {
    private GameBoard gameBoard;

    public GameBoardView() {
    }

    public GameBoardView(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

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
 * Serialization utility.
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

/**
 * Main runner for the text-based game.
 */
class InertiaTextGame {
    public InertiaTextGame() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            return;
        }
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return;
        }
        try {
            GameState gameState = GameStateSerializer.loadFrom(Path.of(input));
            GameController controller = new GameController(gameState);

            while (true) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase("Quit")) {
                    break;
                }
                if (command.equalsIgnoreCase("Undo") || command.equalsIgnoreCase("U")) {
                    controller.processUndo();
                    gameState.getGameBoardView().output(false);
                    continue;
                }
                Direction direction = parseDirection(command);
                if (direction != null) {
                    controller.processMove(direction);
                    gameState.getGameBoardView().output(false);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Direction parseDirection(String command) {
        if (command.equalsIgnoreCase("U") || command.equalsIgnoreCase("Up")) {
            return Direction.UP;
        }
        if (command.equalsIgnoreCase("D") || command.equalsIgnoreCase("Down")) {
            return Direction.DOWN;
        }
        if (command.equalsIgnoreCase("L") || command.equalsIgnoreCase("Left")) {
            return Direction.LEFT;
        }
        if (command.equalsIgnoreCase("R") || command.equalsIgnoreCase("Right")) {
            return Direction.RIGHT;
        }
        return null;
    }
}