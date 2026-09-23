import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * All classes for the Inertia puzzle game, consolidated into a single file.
 * Each class is package-private so that only FJC.java contains public types.
 */

// ===================== Position =====================
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

// ===================== PositionOffset =====================
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

// ===================== Direction =====================
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

// ===================== BoardElement =====================
interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

// ===================== Cell =====================
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

// ===================== Wall =====================
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

// ===================== Entity =====================
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
}

// ===================== ExtraLife =====================
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

// ===================== Gem =====================
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

// ===================== Mine =====================
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

// ===================== Player =====================
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

// ===================== EntityCell =====================
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
            entity.setowner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            entity.setowner(this);
        }
    }

    public Entity setentity(Entity newEntity) {
        Entity old = this.entity;
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return old;
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

// ===================== StopCell =====================
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

    public Entity setentity(Entity newEntity) {
        Entity old = getEntity();
        setEntity(newEntity);
        return old;
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

// ===================== GameBoard =====================
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
        // Locate player in board
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
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

// ===================== MoveResult =====================
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

// ===================== Valid =====================
class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
        super();
        this.origPosition = new Position();
    }

    public Valid(Position newPosition, Position origPosition) {
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

// ===================== Alive =====================
class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

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
}

// ===================== Dead =====================
class Dead extends Valid {
    public Position minePosition;

    public Dead() {
        super();
        this.minePosition = new Position();
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

// ===================== Invalid =====================
class Invalid extends MoveResult {
    public Invalid() {
        super();
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}

// ===================== MoveStack =====================
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

// ===================== GameBoardController =====================
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
        if (gameBoard == null || gameBoard.player == null) {
            return new Invalid();
        }

        Position playerPos = gameBoard.player.getOwner() != null
                ? gameBoard.player.getOwner().getPosition()
                : null;
        if (playerPos == null) {
            return new Invalid();
        }

        Position origPosition = new Position(playerPos.getRow(), playerPos.getCol());
        PositionOffset offset = direction.getOffset();
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position mineHitPosition = null;

        Position current = new Position(playerPos.getRow(), playerPos.getCol());
        Position previous = new Position(playerPos.getRow(), playerPos.getCol());
        boolean moved = false;

        while (true) {
            Position next = current.offsetByOrNull(offset, gameBoard.numRows, gameBoard.numCols);
            if (next == null) {
                // Out of bounds: stop at current
                break;
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                // Stop just before wall
                break;
            }
            if (nextCell instanceof StopCell) {
                // Stop exactly on stop cell
                previous = current;
                current = next;
                moved = true;
                break;
            }
            if (nextCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) nextCell;
                Entity e = ec.getEntity();
                if (e instanceof Mine) {
                    mineHitPosition = new Position(next.getRow(), next.getCol());
                    break;
                }
                if (e instanceof Gem) {
                    collectedGems.add(new Position(next.getRow(), next.getCol()));
                    ec.setEntity(null);
                } else if (e instanceof ExtraLife) {
                    collectedExtraLives.add(new Position(next.getRow(), next.getCol()));
                    ec.setEntity(null);
                }
            }
            // Continue sliding
            previous = current;
            current = next;
            moved = true;
        }

        if (!moved) {
            return new Invalid(origPosition);
        }

        if (mineHitPosition != null) {
            // Dead: player position remains at original. Don't collect anything.
            // Restore gems/lives that might have been removed (none were, because we break before
            // removing on mine encounter). But we did remove entities before encountering mine.
            // Per requirements: board remains unchanged and items before mine are not collected.
            // So restore them.
            for (Position p : collectedGems) {
                gameBoard.getEntityCell(p).setEntity(new Gem());
            }
            for (Position p : collectedExtraLives) {
                gameBoard.getEntityCell(p).setEntity(new ExtraLife());
            }
            return new Dead(origPosition, origPosition, mineHitPosition);
        }

        // Alive: move player to current position
        EntityCell fromCell = (EntityCell) gameBoard.getCell(origPosition.getRow(), origPosition.getCol());
        EntityCell toCell = (EntityCell) gameBoard.getCell(current.getRow(), current.getCol());

        // Remove player from old position
        fromCell.setEntity(null);

        // Place player on new position
        toCell.setEntity(gameBoard.player);

        Alive alive = new Alive(current, origPosition);
        alive.collectedGems = collectedGems;
        alive.collectedExtraLives = collectedExtraLives;
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;

        // Restore player to original position
        Position orig = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        EntityCell fromCell = (EntityCell) gameBoard.getCell(newPos.getRow(), newPos.getCol());
        EntityCell toCell = (EntityCell) gameBoard.getCell(orig.getRow(), orig.getCol());

        // Remove player from new pos, put back at original
        fromCell.setEntity(null);
        toCell.setEntity(gameBoard.player);

        // Restore collected gems and extra lives
        for (Position p : alive.collectedGems) {
            gameBoard.getEntityCell(p).setEntity(new Gem());
        }
        for (Position p : alive.collectedExtraLives) {
            gameBoard.getEntityCell(p).setEntity(new ExtraLife());
        }
    }
}

// ===================== GameBoardView =====================
class GameBoardView {

    private final GameBoard gameBoard;

    public GameBoardView(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
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

// ===================== GameState =====================
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
        this.initialNumOfGems = 0;
        this.gameBoard = new GameBoard();
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
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

    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) return UNLIMITED_LIVES;
        this.numLives += delta;
        return this.numLives;
    }

    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) return UNLIMITED_LIVES;
        this.numLives -= delta;
        return this.numLives;
    }

    public int decrementNumLives() {
        if (hasUnlimitedLives()) return UNLIMITED_LIVES;
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
        int collected = initialNumOfGems - getNumGems();
        int undoes = moveStack.getPopCount();
        return initialNumOfGems + (collected * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }
}

// ===================== GameController =====================
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
            // Do not change counters; do not push to stack
            return result;
        } else if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            // Apply extra life effects
            gameState.increaseNumLives(alive.collectedExtraLives.size());
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            // Not pushed to stack
        }
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult move = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(move);
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            // Reverse finite ExtraLife gains
            if (!gameState.hasUnlimitedLives()) {
                gameState.decreaseNumLives(alive.collectedExtraLives.size());
            }
        }
        return true;
    }
}

// ===================== GameStateSerializer =====================
class GameStateSerializer {

    private GameStateSerializer() {
    }

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

// ===================== InertiaTextGame =====================
class InertiaTextGame {

    private GameState gameState;
    private GameController controller;
    private Scanner scanner;

    public InertiaTextGame() {
        this.scanner = new Scanner(System.in);
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.controller = new GameController(gameState);
        this.scanner = new Scanner(System.in);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        this.controller = new GameController(gameState);
    }

    public GameController getController() {
        return controller;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void run() {
        System.out.println("Inertia - Commands: U, D, L, R, Undo, NewGame <file>, Save <file>, Quit");
        boolean running = true;
        while (running) {
            if (gameState != null) {
                gameState.getGameBoardView().output(true);
                System.out.println("Lives: " + (gameState.hasUnlimitedLives() ? "Unlimited" : gameState.getNumLives())
                        + "  Moves: " + gameState.getNumMoves()
                        + "  Gems left: " + gameState.getNumGems()
                        + "  Score: " + gameState.getScore());

                if (gameState.hasWon()) {
                    System.out.println("You won!");
                    running = false;
                    continue;
                }
                if (gameState.hasLost()) {
                    System.out.println("You lost!");
                    running = false;
                    continue;
                }
            }

            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0];
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "U":
                case "u":
                    if (gameState != null) controller.processMove(Direction.UP);
                    break;
                case "D":
                case "d":
                    if (gameState != null) controller.processMove(Direction.DOWN);
                    break;
                case "L":
                case "l":
                    if (gameState != null) controller.processMove(Direction.LEFT);
                    break;
                case "R":
                case "r":
                    if (gameState != null) controller.processMove(Direction.RIGHT);
                    break;
                case "Undo":
                case "undo":
                    if (gameState != null) {
                        boolean ok = controller.processUndo();
                        if (!ok) System.out.println("Nothing to undo.");
                    }
                    break;
                case "NewGame":
                case "newgame": {
                    try {
                        Path p = Paths.get(arg);
                        GameState gs = GameStateSerializer.loadFrom(p);
                        setGameState(gs);
                    } catch (Exception e) {
                        System.out.println("Failed to load: " + e.getMessage());
                    }
                    break;
                }
                case "Save":
                case "save": {
                    try {
                        Path p = Paths.get(arg);
                        GameStateSerializer.writeTo(gameState, p);
                        System.out.println("Saved.");
                    } catch (Exception e) {
                        System.out.println("Failed to save: " + e.getMessage());
                    }
                    break;
                }
                case "Quit":
                case "quit":
                case "Q":
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        InertiaTextGame game = new InertiaTextGame();
        if (args.length > 0) {
            try {
                Path p = Paths.get(args[0]);
                GameState gs = GameStateSerializer.loadFrom(p);
                game.setGameState(gs);
            } catch (Exception e) {
                System.out.println("Failed to load initial file: " + e.getMessage());
                return;
            }
        }
        game.run();
    }
}