import java.util.*;
import java.io.*;
import java.nio.file.*;

interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

abstract class Cell implements BoardElement {
    private Position position;
    public Cell() {}
    public Cell(Position p) { this.position = p; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT;
    public PositionOffset getOffset() {
        return switch(this) {
            case UP -> new PositionOffset(-1, 0);
            case DOWN -> new PositionOffset(1, 0);
            case LEFT -> new PositionOffset(0, -1);
            case RIGHT -> new PositionOffset(0, 1);
        };
    }
    public int getRowOffset() { return getOffset().getDRow(); }
    public int getColOffset() { return getOffset().getDCol(); }
}

abstract class Entity implements BoardElement {
    private EntityCell owner;
    public Entity() {}
    public EntityCell getOwner() { return owner; }
    public EntityCell setOwner(EntityCell owner) { this.owner = owner; return owner; }
    public EntityCell setowner(EntityCell owner) { EntityCell prev = this.owner; this.owner = owner; return prev; }
}

class EntityCell extends Cell {
    private Entity entity;
    public EntityCell() {}
    public EntityCell(Position p) { super(p); }
    public EntityCell(Position p, Entity e) { super(p); setEntity(e); }
    public Entity getEntity() { return entity; }
    public Entity setEntity(Entity newEntity) { 
        this.entity = newEntity; 
        if (newEntity != null) newEntity.setOwner(this);
        return newEntity; 
    }
    public Entity setentity(Entity newEntity) {
        Entity prev = this.entity;
        if (this.entity != null) this.entity.setowner(null);
        this.entity = newEntity;
        if (newEntity != null) newEntity.setowner(this);
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

class ExtraLife extends Entity {
    @Override
    public char toUnicodeChar() { return '\u2661'; }
    @Override
    public char toASCIIChar() { return 'L'; }
}

class GameBoard {
    public int numRows, numCols;
    public Cell[][] board;
    public Player player;
    public GameBoard() {}
    public GameBoard(int r, int c, Cell[][] b) { this.numRows = r; this.numCols = c; this.board = b; }
    public int getNumRows() { return numRows; }
    public void setNumRows(int numRows) { this.numRows = numRows; }
    public int getNumCols() { return numCols; }
    public void setNumCols(int numCols) { this.numCols = numCols; }
    public Cell[][] getBoard() { return board; }
    public void setBoard(Cell[][] board) { this.board = board; }
    public Cell[] getRow(int r) { return board[r]; }
    public Cell[] getCol(int c) { Cell[] col = new Cell[numRows]; for(int i=0; i<numRows; i++) col[i] = board[i][c]; return col; }
    public Cell getCell(int r, int c) { return board[r][c]; }
    public int getNumGems() { int count = 0; for(Cell[] row : board) for(Cell c : row) if(c instanceof EntityCell && ((EntityCell)c).getEntity() instanceof Gem) count++; return count; }
    public EntityCell getEntityCell(int r, int c) { return (EntityCell)board[r][c]; }
    public EntityCell getEntityCell(Position p) { return (EntityCell)board[p.getRow()][p.getCol()]; }
}

class GameBoardController {
    private GameBoard gameBoard;
    public GameBoardController() {}
    public GameBoard getGameBoard() { return gameBoard; }
    public void setGameBoard(GameBoard gb) { this.gameBoard = gb; }
    public MoveResult makeMove(Direction d) { return null; }
    public void undoMove(MoveResult prevMove) {}
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

class GameController {
    private GameState gameState;
    public GameController() {}
    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gs) { this.gameState = gs; }
    public MoveResult processMove(Direction d) { return null; }
    public boolean processUndo() { return false; }
}

class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths, numMoves, numLives, initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    public GameState() {}
    public GameState(GameBoard gb) { this.gameBoard = gb; }
    public GameState(GameBoard gb, int lives) { this.gameBoard = gb; this.numLives = lives; }
    public boolean hasWon() { return getNumGems() == 0; }
    public boolean hasLost() { return numLives == 0; }
    public boolean hasUnlimitedLives() { return numLives < 0; }
    public int increaseNumLives(int delta) { return numLives += delta; }
    public int decreaseNumLives(int delta) { return numLives -= delta; }
    public int decrementNumLives() { return --numLives; }
    public int incrementNumMoves() { return ++numMoves; }
    public int incrementNumDeaths() { return ++numDeaths; }
    public int getNumGems() { return gameBoard.getNumGems(); }
    public int getScore() { return 0; }
    public GameBoardController getGameBoardController() { return null; }
    public GameBoardView getGameBoardView() { return null; }
    public GameBoard getGameBoard() { return gameBoard; }
    public MoveStack getMoveStack() { return moveStack; }
    public void setGameBoard(GameBoard gb) { this.gameBoard = gb; }
    public void setMoveStack(MoveStack ms) { this.moveStack = ms; }
}

class Gem extends Entity {
    @Override
    public char toUnicodeChar() { return '\u25C7'; }
    @Override
    public char toASCIIChar() { return '*'; }
}

class Mine extends Entity {
    @Override
    public char toUnicodeChar() { return '\u26A0'; }
    @Override
    public char toASCIIChar() { return 'X'; }
}

abstract class MoveResult {
    private Position newPosition;
    public Position getNewPosition() { return newPosition; }
    public void setNewPosition(Position p) { this.newPosition = p; }
}

class Valid extends MoveResult {
    private Position origPosition;
    public Position getOrigPosition() { return origPosition; }
    public void setOrigPosition(Position p) { this.origPosition = p; }
}

class Alive extends Valid {
    private List<Position> collectedGems = new ArrayList<>();
    private List<Position> collectedExtraLives = new ArrayList<>();
    public List<Position> getCollectedGems() { return collectedGems; }
    public List<Position> getCollectedExtraLives() { return collectedExtraLives; }
}

class Dead extends Valid {
    private Position minePosition;
    public Position getMinePosition() { return minePosition; }
    public void setMinePosition(Position p) { this.minePosition = p; }
}

class Invalid extends MoveResult {}

class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new ArrayDeque<>();
    public void push(MoveResult m) { stack.push(m); }
    public MoveResult pop() { popCount++; return stack.pop(); }
    public MoveResult peek() { return stack.peek(); }
    public boolean isEmpty() { return stack.isEmpty(); }
}

class Player extends Entity {
    @Override
    public char toUnicodeChar() { return '\u25EF'; }
    @Override
    public char toASCIIChar() { return '@'; }
}

class Position {
    private int row, col;
    public Position() {}
    public Position(int r, int c) { this.row = r; this.col = c; }
    public int getRow() { return row; }
    public void setRow(int r) { this.row = r; }
    public int getCol() { return col; }
    public void setCol(int c) { this.col = c; }
    public Position offsetBy(int dr, int dc) { return new Position(row + dr, col + dc); }
    public Position offsetBy(PositionOffset o) { return offsetBy(o.getDRow(), o.getDCol()); }
    public Position offsetByOrNull(int dr, int dc, int nr, int nc) { return null; }
    public Position offsetByOrNull(PositionOffset o, int nr, int nc) { return null; }
}

class PositionOffset {
    private int dRow, dCol;
    public PositionOffset() {}
    public PositionOffset(int dr, int dc) { this.dRow = dr; this.dCol = dc; }
    public int getDRow() { return dRow; }
    public void setDRow(int dr) { this.dRow = dr; }
    public int getDCol() { return dCol; }
    public void setDCol(int dc) { this.dCol = dc; }
}

class StopCell extends EntityCell {
    public StopCell() {}
    public StopCell(Position p) { super(p); }
    public StopCell(Position p, Player pl) { super(p, pl); }
    @Override public Entity setEntity(Entity newEntity) { return super.setEntity(newEntity); }
    public Entity setentity(Entity newEntity) { return super.setentity(newEntity); }
    public Player setPlayer(Player p) { setEntity(p); return p; }
    @Override
    public char toUnicodeChar() { return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1'; }
    @Override
    public char toASCIIChar() { return getEntity() != null ? getEntity().toASCIIChar() : '#'; }
}

class Wall extends Cell {
    public Wall() {}
    public Wall(Position p) { super(p); }
    @Override
    public char toUnicodeChar() { return '\u2588'; }
    @Override
    public char toASCIIChar() { return 'W'; }
}

class GameStateSerializer {
    private GameStateSerializer() {}
    public static Path writeTo(final GameState gameState, final Path outputFile) throws FileAlreadyExistsException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(outputFile);
        if (Files.exists(outputFile)) throw new FileAlreadyExistsException(outputFile.toString());
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) { writeTo(gameState, writer); } 
        catch (final IOException e) { throw new RuntimeException(e); }
        return outputFile;
    }
    static void writeTo(final GameState gameState, final BufferedWriter writer) throws IOException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(writer);
        writer.write(Integer.toString(gameState.getGameBoard().getNumRows()));
        writer.newLine();
        writer.write(Integer.toString(gameState.getGameBoard().getNumCols()));
        writer.newLine();
        if (gameState.hasUnlimitedLives()) writer.write("");
        else writer.write(Integer.toString(gameState.getNumLives()));
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
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) { return loadFrom(reader); } 
        catch (IOException e) { throw new RuntimeException(e); }
    }
    static GameState loadFrom(final BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        final int numRows = Integer.parseInt(reader.readLine());
        final int numCols = Integer.parseInt(reader.readLine());
        final int numLives;
        {
            final String line = reader.readLine();
            numLives = line.isBlank() ? -1 : Integer.parseInt(line);
        }
        final Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            final String line = reader.readLine();
            for (int c = 0; c < numCols; ++c) board[r][c] = fromCellChar(line.charAt(c), new Position(r, c));
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
            default -> throw new IllegalArgumentException("Unknown cell representation: " + c);
        };
    }
}