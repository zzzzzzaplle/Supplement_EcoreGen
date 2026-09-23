import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

// BoardElement interface
interface BoardElement {
    char toUnicodeChar();
    char toASCIIChar();
}

// Position class
class Position {
    private int row;
    private int col;

    public Position() {
        this(0, 0);
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
        int newRow = row + dRow;
        int newCol = col + dCol;
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
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Position{" + "row=" + row + ", col=" + col + '}';
    }
}

// PositionOffset class
class PositionOffset {
    private int dRow;
    private int dCol;

    public PositionOffset() {
        this(0, 0);
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

// Direction enum
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowOffset;
    private final int colOffset;

    Direction(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    public PositionOffset getOffset() {
        return new PositionOffset(rowOffset, colOffset);
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getColOffset() {
        return colOffset;
    }
}

// Abstract Cell class
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

// Wall class
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

// Abstract Entity class
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

// EntityCell class
class EntityCell extends Cell {
    Entity entity;

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

// StopCell class
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

    public Entity setEntity(Entity newEntity) {
        return super.setEntity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        Entity old = super.setEntity(newPlayer);
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

// ExtraLife class
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

// Gem class
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

// Mine class
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

// Player class
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

// Abstract MoveResult class
abstract class MoveResult {
    private Position newPosition;

    public MoveResult() {
        this.newPosition = null;
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

// Valid class
class Valid extends MoveResult {
    private Position origPosition;

    public Valid() {
        super();
        this.origPosition = null;
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

// Alive class
class Alive extends Valid {
    private List<Position> collectedGems;
    private List<Position> collectedExtraLives;

    public Alive() {
        super();
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position origPosition, Position newPosition) {
        super(origPosition, newPosition);
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

// Dead class
class Dead extends Valid {
    private Position minePosition;

    public Dead() {
        super();
        this.minePosition = null;
    }

    public Dead(Position origPosition, Position newPosition, Position minePosition) {
        super(origPosition, newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}

// Invalid class
class Invalid extends MoveResult {
    public Invalid() {
        super();
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}

// MoveStack class
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
        if (!stack.isEmpty()) {
            popCount++;
            return stack.pop();
        }
        return null;
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

// GameBoard class
class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;
    private Player player;

    public GameBoard() {
        this(0, 0, new Cell[0][0]);
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = findPlayer();
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

    private Player findPlayer() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] instanceof EntityCell) {
                    EntityCell ec = (EntityCell) board[r][c];
                    if (ec.getEntity() instanceof Player) {
                        return (Player) ec.getEntity();
                    }
                }
            }
        }
        return null;
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
                if (board[r][c] instanceof EntityCell) {
                    EntityCell ec = (EntityCell) board[r][c];
                    if (ec.getEntity() instanceof Gem) {
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

// GameBoardController class
class GameBoardController {
    private GameBoard gameBoard;

    public GameBoardController() {
        this.gameBoard = null;
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
        Player player = gameBoard.getPlayer();
        if (player == null) {
            return new Invalid(null);
        }

        Position currentPos = player.getOwner().getPosition();
        PositionOffset offset = direction.getOffset();
        int dRow = offset.getDRow();
        int dCol = offset.getDCol();

        // Try first step
        Position nextPos = currentPos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
        if (nextPos == null) {
            return new Invalid(currentPos);
        }

        Cell nextCell = gameBoard.getCell(nextPos.getRow(), nextPos.getCol());
        if (nextCell instanceof Wall) {
            return new Invalid(currentPos);
        }

        // Start sliding
        Position origPosition = currentPos;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position slidePos = currentPos;
        Position minePos = null;
        boolean hitMine = false;

        while (true) {
            Position candidatePos = slidePos.offsetByOrNull(dRow, dCol, gameBoard.getNumRows(), gameBoard.getNumCols());
            if (candidatePos == null) {
                // Boundary stop
                break;
            }

            Cell candidateCell = gameBoard.getCell(candidatePos.getRow(), candidatePos.getCol());
            if (candidateCell instanceof Wall) {
                // Stop before wall
                break;
            }

            slidePos = candidatePos;

            if (candidateCell instanceof EntityCell) {
                EntityCell ec = (EntityCell) candidateCell;
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    hitMine = true;
                    minePos = candidatePos;
                    break;
                } else if (entity instanceof Gem) {
                    collectedGems.add(candidatePos);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(candidatePos);
                }
            }

            if (candidateCell instanceof StopCell) {
                // Stop exactly on StopCell
                break;
            }
        }

        if (slidePos.equals(currentPos)) {
            // No movement at all
            return new Invalid(currentPos);
        }

        if (hitMine) {
            // Dead result: player stays at original position, board unchanged
            return new Dead(origPosition, origPosition, minePos);
        }

        // Alive move: move player to slidePos, collect items
        // Remove player from current cell
        EntityCell currentCell = (EntityCell) gameBoard.getCell(currentPos.getRow(), currentPos.getCol());
        currentCell.setEntity(null);

        // Place player at destination
        EntityCell destCell = (EntityCell) gameBoard.getCell(slidePos.getRow(), slidePos.getCol());
        destCell.setEntity(player);

        // Remove collected gems and extra lives
        for (Position gemPos : collectedGems) {
            EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
            gemCell.setEntity(null);
        }
        for (Position lifePos : collectedExtraLives) {
            EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
            lifeCell.setEntity(null);
        }

        Alive aliveResult = new Alive(origPosition, slidePos);
        aliveResult.getCollectedGems().addAll(collectedGems);
        aliveResult.getCollectedExtraLives().addAll(collectedExtraLives);
        return aliveResult;
    }

    public void undoMove(MoveResult prevMove) {
        if (prevMove instanceof Alive) {
            Alive alive = (Alive) prevMove;
            Position origPos = alive.getOrigPosition();
            Position newPos = alive.getNewPosition();

            // Move player back
            EntityCell destCell = (EntityCell) gameBoard.getCell(origPos.getRow(), origPos.getCol());
            EntityCell sourceCell = (EntityCell) gameBoard.getCell(newPos.getRow(), newPos.getCol());
            Player player = (Player) sourceCell.getEntity();
            sourceCell.setEntity(null);
            destCell.setEntity(player);

            // Restore gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = (EntityCell) gameBoard.getCell(gemPos.getRow(), gemPos.getCol());
                gemCell.setEntity(new Gem());
            }

            // Restore extra lives
            for (Position lifePos : alive.getCollectedExtraLives()) {
                EntityCell lifeCell = (EntityCell) gameBoard.getCell(lifePos.getRow(), lifePos.getCol());
                lifeCell.setEntity(new ExtraLife());
            }
        }
    }
}

// GameBoardView class (verbatim from reference)
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

    public GameBoard getGameBoard() {
        return gameBoard;
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

// GameState class
class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = 3;
        this.initialNumOfGems = 0;
        this.gameBoard = null;
        this.moveStack = new MoveStack();
    }

    public GameState(GameBoard gameBoard) {
        this();
        this.gameBoard = gameBoard;
        this.numLives = -1;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this();
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
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
        if (hasUnlimitedLives()) {
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
        if (hasUnlimitedLives()) {
            return false;
        }
        return numLives <= 0;
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
            if (numLives < 0) numLives = 0;
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
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int boardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int undoes = moveStack.getPopCount();
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }
}

// GameController class
class GameController {
    private GameState gameState;

    public GameController() {
        this.gameState = null;
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
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed to MoveStack
            return result;
        } else if (result instanceof Alive) {
            // Alive moves increment numMoves, apply collected ExtraLife effects, push to MoveStack
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths, decrease finite lives by one, not pushed to MoveStack
            gameState.incrementNumMoves();
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
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            // Reverse extra life gains
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
            // Restore board state
            GameBoardController controller = gameState.getGameBoardController();
            controller.undoMove(move);
            return true;
        }
        return false;
    }
}

// GameStateSerializer class (verbatim from reference)
class GameStateSerializer {

    private GameStateSerializer() {
    }

    /**
     * Serializes the specified {@link GameState} object to the output file.
     *
     * @param gameState  The game state instance to write to the file.
     * @param outputFile The file to write to.
     * @return {@code outputFile}.
     * @throws java.nio.file.FileAlreadyExistsException if a file or directory already exists with the same path as
     *                                    {@code outputFile}.
     */
    public static java.nio.file.Path writeTo(final GameState gameState, final java.nio.file.Path outputFile)
            throws java.nio.file.FileAlreadyExistsException {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(outputFile);

        if (java.nio.file.Files.exists(outputFile)) {
            throw new java.nio.file.FileAlreadyExistsException(outputFile.toString());
        }

        try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFile)) {
            writeTo(gameState, writer);
        } catch (final java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    /**
     * Serializes the specified {@link GameState} object into the provided {@link java.io.BufferedWriter}.
     *
     * @param gameState The game state to serialize.
     * @param writer    The writer to write the serialized game state to.
     * @throws java.io.IOException If an I/O error occurred while writing to {@code writer}.
     * @apiNote The caller is responsible for closing {@code writer}.
     */
    static void writeTo(final GameState gameState, final java.io.BufferedWriter writer)
            throws java.io.IOException {
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
     * @throws java.io.FileNotFoundException if {@code inputFile} does not exist.
     */
    public static GameState loadFrom(final java.nio.file.Path inputFile)
            throws java.io.FileNotFoundException {
        Objects.requireNonNull(inputFile);

        if (!java.nio.file.Files.isRegularFile(inputFile)) {
            throw new java.io.FileNotFoundException(inputFile.toString());
        }

        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(inputFile)) {
            return loadFrom(reader);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link GameState} instance by reading from the {@link java.io.BufferedReader}.
     *
     * @param reader The reader providing the serialized version of the game state.
     * @return An instance of {@link GameState} created from deserializing {@code reader}.
     * @throws java.io.IOException If an I/O error occurred while reading from {@code reader}.
     * @apiNote The caller is responsible for closing {@code reader}.
     */
    static GameState loadFrom(final java.io.BufferedReader reader) throws java.io.IOException {
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

// InertiaTextGame class
class InertiaTextGame {
    private GameController gameController;
    private GameState gameState;

    public InertiaTextGame() {
        this.gameController = null;
        this.gameState = null;
    }

    public InertiaTextGame(GameState gameState) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void run() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean useUnicode = true;

        while (true) {
            // Display board
            GameBoardView view = gameState.getGameBoardView();
            view.output(useUnicode);

            System.out.println("Score: " + gameState.getScore());
            System.out.println("Lives: " + (gameState.hasUnlimitedLives() ? "Unlimited" : gameState.getNumLives()));
            System.out.println("Moves: " + gameState.getNumMoves());
            System.out.println("Deaths: " + gameState.getNumDeaths());

            if (gameState.hasWon()) {
                System.out.println("Congratulations! You won!");
                break;
            }
            if (gameState.hasLost()) {
                System.out.println("Game Over! You lost!");
                break;
            }

            System.out.print("Enter command (U/D/L/R/Undo/Quit): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("QUIT")) {
                break;
            } else if (input.equals("UNDO")) {
                if (gameController.processUndo()) {
                    System.out.println("Undo successful.");
                } else {
                    System.out.println("No moves to undo.");
                }
            } else {
                Direction direction = null;
                switch (input) {
                    case "U":
                        direction = Direction.UP;
                        break;
                    case "D":
                        direction = Direction.DOWN;
                        break;
                    case "L":
                        direction = Direction.LEFT;
                        break;
                    case "R":
                        direction = Direction.RIGHT;
                        break;
                    default:
                        System.out.println("Invalid command. Use U, D, L, R, Undo, or Quit.");
                        continue;
                }

                MoveResult result = gameController.processMove(direction);
                if (result instanceof Invalid) {
                    System.out.println("Invalid move - cannot move in that direction.");
                } else if (result instanceof Dead) {
                    System.out.println("You hit a mine! Lost a life.");
                } else if (result instanceof Alive) {
                    Alive alive = (Alive) result;
                    if (!alive.getCollectedGems().isEmpty()) {
                        System.out.println("Collected " + alive.getCollectedGems().size() + " gem(s)!");
                    }
                    if (!alive.getCollectedExtraLives().isEmpty()) {
                        System.out.println("Collected " + alive.getCollectedExtraLives().size() + " extra life(s)!");
                    }
                }
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                GameState state = GameStateSerializer.loadFrom(java.nio.file.Paths.get(args[0]));
                InertiaTextGame game = new InertiaTextGame(state);
                game.run();
            } catch (Exception e) {
                System.err.println("Error loading game: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.println("Usage: java InertiaTextGame <savefile>");
            System.exit(1);
        }
    }
}