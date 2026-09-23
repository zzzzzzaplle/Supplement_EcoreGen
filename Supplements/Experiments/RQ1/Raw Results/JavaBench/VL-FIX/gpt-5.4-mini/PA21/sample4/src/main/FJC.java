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
import java.util.Scanner;

/**
 * Common board element interface for all visual/game board elements.
 */
interface BoardElement {
    char toUnicodeChar();

    char toASCIIChar();
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

    public Position offsetBy(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        Objects.requireNonNull(offset);
        return offsetBy(offset.getDRow(), offset.getDCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int nr = row + dRow;
        int nc = col + dCol;
        if (nr < 0 || nr >= numRows || nc < 0 || nc >= numCols) {
            return null;
        }
        return new Position(nr, nc);
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        Objects.requireNonNull(offset);
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

/**
 * A row/column offset.
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
 * Directions for movement.
 */
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public PositionOffset getOffset() {
        return new PositionOffset(getRowOffset(), getColOffset());
    }

    public int getRowOffset() {
        return switch (this) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
    }

    public int getColOffset() {
        return switch (this) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }
}

/**
 * Abstract board cell.
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
 * Abstract entity class.
 */
abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell setowner(EntityCell ownner) {
        this.owner = ownner;
        return ownner;
    }

    public EntityCell getOwner() {
        return owner;
    }

    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }
}

/**
 * A cell that can contain an entity.
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
        Entity old = this.entity;
        this.entity = newEntity;
        if (old != null) {
            old.setOwner(null);
        }
        if (newEntity != null) {
            newEntity.setOwner(this);
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
        return super.setEntity(newEntity);
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
 * An extra life entity.
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
 * A gem entity.
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
 * A mine entity.
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
 * A player entity.
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
 * A move result base type.
 */
abstract class MoveResult {
    private Position newPosition;

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
 * A valid move result.
 */
class Valid extends MoveResult {
    private Position origPosition;

    public Valid() {
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

/**
 * An alive move result.
 */
class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
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
 * A dead move result.
 */
class Dead extends Valid {
    private Position minePosition;

    public Dead() {
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
 * An invalid move result.
 */
class Invalid extends MoveResult {
    public Invalid() {
    }
}

/**
 * A move stack supporting undo.
 */
class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
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
 * A game board.
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
        locatePlayer();
    }

    public GameBoard(int numRows, int numCols, Cell[][] board, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = player;
    }

    private void locatePlayer() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] instanceof EntityCell) {
                    Entity e = ((EntityCell) board[r][c]).getEntity();
                    if (e instanceof Player) {
                        player = (Player) e;
                        return;
                    }
                }
            }
        }
        if (player == null) {
            player = new Player();
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
        for (int r = 0; r < numRows; r++) col[r] = board[r][c];
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
                    if (e instanceof Gem) count++;
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

    public Position findPlayerPosition() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) return new Position(r, c);
                }
            }
        }
        return null;
    }
}

/**
 * Controller for making and undoing moves on the board.
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
        Objects.requireNonNull(direction);
        Position start = gameBoard.findPlayerPosition();
        if (start == null) return new Invalid();
        Position current = start;
        List<Position> gems = new ArrayList<>();
        List<Position> lives = new ArrayList<>();
        while (true) {
            Position next = current.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(), gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                if (current.equals(start)) return new Invalid();
                Alive alive = new Alive(current, start);
                alive.setCollectedGems(gems);
                alive.setCollectedExtraLives(lives);
                return alive;
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                if (current.equals(start)) return new Invalid();
                Alive alive = new Alive(current, start);
                alive.setCollectedGems(gems);
                alive.setCollectedExtraLives(lives);
                return alive;
            }
            if (nextCell instanceof StopCell) {
                if (current.equals(start) && ((StopCell) nextCell).getEntity() == null) return new Invalid();
                if (((StopCell) nextCell).getEntity() instanceof Player || ((StopCell) nextCell).getEntity() == null) {
                    Alive alive = new Alive(next, start);
                    alive.setCollectedGems(gems);
                    alive.setCollectedExtraLives(lives);
                    return alive;
                }
            }
            if (nextCell instanceof EntityCell) {
                Entity entity = ((EntityCell) nextCell).getEntity();
                if (entity instanceof Mine) {
                    return new Dead(start, start, next);
                }
                if (entity instanceof Gem) gems.add(next);
                if (entity instanceof ExtraLife) lives.add(next);
            }
            current = next;
        }
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) return;
        Alive alive = (Alive) prevMove;
        Position orig = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();
        gameBoard.getEntityCell(orig).setEntity(gameBoard.getEntityCell(newPos).getEntity());
        gameBoard.getEntityCell(newPos).setEntity(null);
        for (Position p : alive.getCollectedGems()) {
            gameBoard.getEntityCell(p).setEntity(new Gem());
        }
        for (Position p : alive.getCollectedExtraLives()) {
            gameBoard.getEntityCell(p).setEntity(new ExtraLife());
        }
    }
}

/**
 * View for a game board.
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
 * Encapsulates overall game state.
 */
class GameState {
    public static int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
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
        if (!hasUnlimitedLives()) numLives += delta;
        return getNumLives();
    }

    public int decreaseNumLives(int delta) {
        if (!hasUnlimitedLives()) numLives -= delta;
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
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - getNumGems();
        return initialBoardSize + (collectedGems * 10) - (numMoves) - (moveStack.getPopCount() * 2) - (numDeaths * 4);
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

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }
}

/**
 * Game controller coordinating state and board controller.
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
        if (result instanceof Invalid) return result;
        gameState.incrementNumMoves();
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
                gameState.getGameBoard().getEntityCell(p).setEntity(null);
            }
            for (Position p : alive.getCollectedGems()) {
                gameState.getGameBoard().getEntityCell(p).setEntity(null);
            }
            Position orig = alive.getOrigPosition();
            Position dest = alive.getNewPosition();
            Entity player = gameState.getGameBoard().getEntityCell(orig).getEntity();
            gameState.getGameBoard().getEntityCell(orig).setEntity(null);
            gameState.getGameBoard().getEntityCell(dest).setEntity(player);
            gameState.getMoveStack().push(alive);
        } else if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) return false;
        MoveResult m = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(m);
        return true;
    }
}

/**
 * Serialization/deserialization utility for game state.
 */
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

/**
 * Main runner for the text game.
 */
class InertiaTextGame {
    public InertiaTextGame() {
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        GameState gameState = null;
        GameController controller = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            if (gameState == null) {
                Path path = Path.of(line);
                gameState = GameStateSerializer.loadFrom(path);
                controller = new GameController(gameState);
                continue;
            }

            if (line.equalsIgnoreCase("Quit")) {
                break;
            }
            if (line.equalsIgnoreCase("Undo") || line.equalsIgnoreCase("U")) {
                controller.processUndo();
                gameState.getGameBoardView().output(false);
                continue;
            }
            Direction direction = parseDirection(line);
            if (direction != null) {
                controller.processMove(direction);
                gameState.getGameBoardView().output(false);
            }
        }
    }

    private static Direction parseDirection(String s) {
        return switch (s.toUpperCase()) {
            case "U", "UP" -> Direction.UP;
            case "D", "DOWN" -> Direction.DOWN;
            case "L", "LEFT" -> Direction.LEFT;
            case "R", "RIGHT" -> Direction.RIGHT;
            default -> null;
        };
    }
}