import java.util.*;
import java.io.*;
import java.nio.file.*;

// --- Interfaces and Enums ---

interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

enum Direction {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
    private final int rowOffset;
    private final int colOffset;
    Direction(int r, int c) { this.rowOffset = r; this.colOffset = c; }
    public PositionOffset getOffset() { return new PositionOffset(rowOffset, colOffset); }
    public int getRowOffset() { return rowOffset; }
    public int getColOffset() { return colOffset; }
}

// --- Supporting Classes ---

class Position {
    private int row, col;
    public Position() {}
    public Position(int row, int col) { this.row = row; this.col = col; }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    public Position offsetBy(int dRow, int dCol) { return new Position(row + dRow, col + dCol); }
    public Position offsetBy(PositionOffset offset) { return new Position(row + offset.getDRow(), col + offset.getDCol()); }
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int r = row + dRow; int c = col + dCol;
        return (r >= 0 && r < numRows && c >= 0 && c < numCols) ? new Position(r, c) : null;
    }
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) { return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols); }
}

class PositionOffset {
    private int dRow, dCol;
    public PositionOffset() {}
    public PositionOffset(int dRow, int dCol) { this.dRow = dRow; this.dCol = dCol; }
    public int getDRow() { return dRow; }
    public void setDRow(int dRow) { this.dRow = dRow; }
    public int getDCol() { return dCol; }
    public void setDCol(int dCol) { this.dCol = dCol; }
}

// --- Abstract Classes ---

abstract class Cell implements BoardElement {
    private Position position;
    public Cell() {}
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
}

abstract class Entity implements BoardElement {
    private EntityCell owner;
    public Entity() {}
    public EntityCell getOwner() { return owner; }
    public EntityCell setOwner(EntityCell owner) { this.owner = owner; return this.owner; }
    public EntityCell setowner(EntityCell owner) { EntityCell prev = this.owner; this.owner = owner; return prev; }
}

abstract class MoveResult {
    private Position newPosition;
    public MoveResult() {}
    public Position getNewPosition() { return newPosition; }
    public void setNewPosition(Position pos) { this.newPosition = pos; }
}

// --- Concrete Cells and Entities ---

class Wall extends Cell {
    public Wall() {}
    public Wall(Position p) { setPosition(p); }
    @Override public char toUnicodeChar() { return '\u2588'; }
    @Override public char toASCIIChar() { return 'W'; }
}

class EntityCell extends Cell {
    private Entity entity;
    public EntityCell() {}
    public EntityCell(Position p) { setPosition(p); }
    public EntityCell(Position p, Entity e) { setPosition(p); setEntity(e); }
    public Entity getEntity() { return entity; }
    public Entity setEntity(Entity newEntity) { this.entity = newEntity; return this.entity; }
    public Entity setentity(Entity newEntity) {
        Entity prev = this.entity;
        if (this.entity != null) this.entity.setowner(null);
        this.entity = newEntity;
        if (newEntity != null) newEntity.setowner(this);
        return prev;
    }
    @Override public char toUnicodeChar() { return getEntity() != null ? getEntity().toUnicodeChar() : '.'; }
    @Override public char toASCIIChar() { return getEntity() != null ? getEntity().toASCIIChar() : '.'; }
}

class StopCell extends EntityCell {
    public StopCell() {}
    public StopCell(Position p) { super(p); }
    public StopCell(Position p, Player player) { super(p, player); }
    @Override public Entity setEntity(Entity newEntity) { return super.setEntity(newEntity); }
    public Entity setentity(Entity newEntity) { return super.setentity(newEntity); }
    public Player setPlayer(Player newPlayer) { setEntity(newPlayer); return newPlayer; }
    @Override public char toUnicodeChar() { return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1'; }
    @Override public char toASCIIChar() { return getEntity() != null ? getEntity().toASCIIChar() : '#'; }
}

class ExtraLife extends Entity {
    public ExtraLife() {}
    @Override public char toUnicodeChar() { return '\u2661'; }
    @Override public char toASCIIChar() { return 'L'; }
}

class Gem extends Entity {
    public Gem() {}
    @Override public char toUnicodeChar() { return '\u25C7'; }
    @Override public char toASCIIChar() { return '*'; }
}

class Mine extends Entity {
    public Mine() {}
    @Override public char toUnicodeChar() { return '\u26A0'; }
    @Override public char toASCIIChar() { return 'X'; }
}

class Player extends Entity {
    public Player() {}
    @Override public char toUnicodeChar() { return '\u25EF'; }
    @Override public char toASCIIChar() { return '@'; }
}

// --- Move Results ---

class Valid extends MoveResult {
    private Position origPosition;
    public Valid() {}
    public Position getOrigPosition() { return origPosition; }
    public void setOrigPosition(Position p) { this.origPosition = p; }
}

class Alive extends Valid {
    private List<Position> collectedGems = new ArrayList<>();
    private List<Position> collectedExtraLives = new ArrayList<>();
    public Alive() {}
    public List<Position> getCollectedGems() { return collectedGems; }
    public void setCollectedGems(List<Position> g) { this.collectedGems = g; }
    public List<Position> getCollectedExtraLives() { return collectedExtraLives; }
    public void setCollectedExtraLives(List<Position> l) { this.collectedExtraLives = l; }
}

class Dead extends Valid {
    private Position minePosition;
    public Dead() {}
    public Position getMinePosition() { return minePosition; }
    public void setMinePosition(Position p) { this.minePosition = p; }
}

class Invalid extends MoveResult {}

// --- Game Logic ---

class GameBoard {
    private int numRows, numCols;
    private Cell[][] board;
    private Player player;
    public GameBoard() {}
    public GameBoard(int r, int c, Cell[][] b) { this.numRows = r; this.numCols = c; this.board = b; }
    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }
    public Cell[] getRow(int r) { return board[r]; }
    public Cell[] getCol(int c) { Cell[] col = new Cell[numRows]; for(int i=0; i<numRows; i++) col[i] = board[i][c]; return col; }
    public Cell getCell(int r, int c) { return board[r][c]; }
    public int getNumGems() { int count = 0; for(Cell[] row : board) for(Cell c : row) if(c instanceof EntityCell && ((EntityCell)c).getEntity() instanceof Gem) count++; return count; }
    public EntityCell getEntityCell(int r, int c) { return (EntityCell)board[r][c]; }
    public EntityCell getEntityCell(Position p) { return getEntityCell(p.getRow(), p.getCol()); }
}

class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new ArrayDeque<>();
    public MoveStack() {}
    public void push(MoveResult m) { stack.push(m); }
    public MoveResult pop() { popCount++; return stack.pop(); }
    public MoveResult peek() { return stack.peek(); }
    public boolean isEmpty() { return stack.isEmpty(); }
}

class GameBoardController {
    private GameBoard gameBoard;
    public GameBoardController() {}
    public MoveResult makeMove(Direction d) { return null; /* Implementation placeholder */ }
    public void undoMove(MoveResult m) {}
}

class GameBoardView {
    private final GameBoard gameBoard;
    public GameBoardView(final GameBoard gameBoard) { this.gameBoard = Objects.requireNonNull(gameBoard); }
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

class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths, numMoves, numLives, initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack = new MoveStack();
    public GameState() {}
    public GameState(GameBoard gb) { this.gameBoard = gb; }
    public GameState(GameBoard gb, int lives) { this.gameBoard = gb; this.numLives = lives; }
    public boolean hasWon() { return getNumGems() == 0; }
    public boolean hasLost() { return numLives == 0; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int getNumLives() { return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives; }
    public int increaseNumLives(int d) { numLives += d; return numLives; }
    public int decreaseNumLives(int d) { numLives -= d; return numLives; }
    public int decrementNumLives() { return --numLives; }
    public int incrementNumMoves() { return ++numMoves; }
    public int incrementNumDeaths() { return ++numDeaths; }
    public int getNumGems() { return gameBoard.getNumGems(); }
    public int getScore() { return 0; /* Logic based on requirements */ }
    public GameBoardController getGameBoardController() { return new GameBoardController(); }
    public GameBoardView getGameBoardView() { return new GameBoardView(gameBoard); }
    public GameBoard getGameBoard() { return gameBoard; }
    public MoveStack getMoveStack() { return moveStack; }
}

class GameController {
    private GameState gameState;
    public GameController() {}
    public MoveResult processMove(Direction d) { return null; }
    public boolean processUndo() { return false; }
}

// --- Serializer (Verbatim provided) ---

class GameStateSerializer {
    private GameStateSerializer() {}
    public static Path writeTo(final GameState gameState, final Path outputFile) throws FileAlreadyExistsException {
        Objects.requireNonNull(gameState); Objects.requireNonNull(outputFile);
        if (Files.exists(outputFile)) throw new FileAlreadyExistsException(outputFile.toString());
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) { writeTo(gameState, writer); } catch (final IOException e) { throw new RuntimeException(e); }
        return outputFile;
    }
    static void writeTo(final GameState gameState, final BufferedWriter writer) throws IOException {
        Objects.requireNonNull(gameState); Objects.requireNonNull(writer);
        writer.write(Integer.toString(gameState.getGameBoard().getNumRows())); writer.newLine();
        writer.write(Integer.toString(gameState.getGameBoard().getNumCols())); writer.newLine();
        if (gameState.hasUnlimitedLives()) writer.write(""); else writer.write(Integer.toString(gameState.getNumLives()));
        writer.newLine();
        for (int r = 0; r < gameState.getGameBoard().getNumRows(); ++r) {
            final Cell[] row = gameState.getGameBoard().getRow(r);
            for (final Cell cell : row) writer.write(toCellChar(cell));
            writer.newLine();
        }
    }
    public static GameState loadFrom(final Path inputFile) throws FileNotFoundException {
        Objects.requireNonNull(inputFile);
        if (!Files.isRegularFile(inputFile)) throw new FileNotFoundException(inputFile.toString());
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) { return loadFrom(reader); } catch (IOException e) { throw new RuntimeException(e); }
    }
    static GameState loadFrom(final BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        final int numRows = Integer.parseInt(reader.readLine());
        final int numCols = Integer.parseInt(reader.readLine());
        final int numLives;
        final String line = reader.readLine();
        numLives = line.isBlank() ? -1 : Integer.parseInt(line);
        final Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            final String rowLine = reader.readLine();
            for (int c = 0; c < numCols; ++c) board[r][c] = fromCellChar(rowLine.charAt(c), new Position(r, c));
        }
        final GameBoard gameBoard = new GameBoard(numRows, numCols, board);
        return numLives < 0 ? new GameState(gameBoard) : new GameState(gameBoard, numLives);
    }
    private static char toCellChar(final Cell cell) {
        Objects.requireNonNull(cell);
        if (cell instanceof Wall) return 'W';
        final EntityCell cellWithEntity = (EntityCell) cell;
        final Entity entity = cellWithEntity.getEntity();
        if (entity instanceof ExtraLife) return 'L';
        if (entity instanceof Gem) return 'G';
        if (entity instanceof Mine) return 'M';
        if (entity instanceof Player) return 'P';
        if (cellWithEntity instanceof StopCell) return 'S';
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
            default -> throw new IllegalArgumentException("Unknown cell: " + c);
        };
    }
}