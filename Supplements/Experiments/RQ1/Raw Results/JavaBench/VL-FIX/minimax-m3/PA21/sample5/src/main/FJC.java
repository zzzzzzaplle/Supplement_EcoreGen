import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

// =============================================================================
// BoardElement interface
// =============================================================================
interface BoardElement {
    char toUnicodeChar();

    char toASCIIChar();
}

// =============================================================================
// Position class
// =============================================================================
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
}

// =============================================================================
// PositionOffset class
// =============================================================================
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

// =============================================================================
// Direction enum
// =============================================================================
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

// =============================================================================
// Cell abstract class
// =============================================================================
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

// =============================================================================
// Entity abstract class
// =============================================================================
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
        return this.owner;
    }
}

// =============================================================================
// EntityCell class
// =============================================================================
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

    public void setEntityField(Entity entity) {
        this.entity = entity;
    }

    public Entity setentity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return this.entity;
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

// =============================================================================
// StopCell class
// =============================================================================
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

// =============================================================================
// Wall class
// =============================================================================
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

// =============================================================================
// ExtraLife class
// =============================================================================
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

// =============================================================================
// Gem class
// =============================================================================
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

// =============================================================================
// Mine class
// =============================================================================
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

// =============================================================================
// Player class
// =============================================================================
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

// =============================================================================
// GameBoard class
// =============================================================================
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
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) {
                        this.player = (Player) e;
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

// =============================================================================
// MoveResult abstract class
// =============================================================================
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

// =============================================================================
// Valid class
// =============================================================================
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

// =============================================================================
// Alive class
// =============================================================================
class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

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

// =============================================================================
// Dead class
// =============================================================================
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

// =============================================================================
// Invalid class
// =============================================================================
class Invalid extends MoveResult {
    public Invalid() {
        super();
    }

    public Invalid(Position newPosition) {
        super(newPosition);
    }
}

// =============================================================================
// MoveStack class
// =============================================================================
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

// =============================================================================
// GameBoardController class
// =============================================================================
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
        if (gameBoard == null || gameBoard.getPlayer() == null) {
            return new Invalid();
        }

        EntityCell playerOwner = gameBoard.getPlayer().getOwner();
        if (playerOwner == null) {
            return new Invalid();
        }
        Position origPosition = new Position(playerOwner.getPosition().getRow(), playerOwner.getPosition().getCol());

        Position current = origPosition;
        Position next = current.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(),
                gameBoard.getNumRows(), gameBoard.getNumCols());

        if (next == null) {
            return new Invalid();
        }

        Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());

        if (nextCell instanceof Wall) {
            return new Invalid();
        }

        if (nextCell instanceof StopCell) {
            StopCell stopCell = (StopCell) nextCell;
            if (stopCell.getEntity() == null) {
                stopCell.setentity(gameBoard.getPlayer());
                playerOwner.setentity(null);
                return new Alive(origPosition, next);
            } else {
                return new Invalid();
            }
        }

        // It's an EntityCell; start sliding
        Position finalPos = origPosition;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position mineHit = null;
        EntityCell lastEntityCell = playerOwner;

        while (next != null) {
            Cell cell = gameBoard.getCell(next.getRow(), next.getCol());

            if (cell instanceof Wall) {
                break;
            }

            if (cell instanceof StopCell) {
                StopCell stopCell = (StopCell) cell;
                if (stopCell.getEntity() == null) {
                    stopCell.setentity(gameBoard.getPlayer());
                    lastEntityCell.setentity(null);
                    finalPos = next;
                }
                break;
            }

            EntityCell ec = (EntityCell) cell;
            Entity entity = ec.getEntity();

            if (entity instanceof Mine) {
                mineHit = next;
                finalPos = origPosition;
                break;
            }

            if (entity instanceof Gem) {
                collectedGems.add(next);
                ec.setentity(null);
                lastEntityCell = ec;
                finalPos = next;
            } else if (entity instanceof ExtraLife) {
                collectedExtraLives.add(next);
                ec.setentity(null);
                lastEntityCell = ec;
                finalPos = next;
            } else if (entity == null) {
                lastEntityCell = ec;
                finalPos = next;
            } else {
                // Player entity on cell; should not happen normally; treat as stop
                break;
            }

            current = next;
            next = current.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(),
                    gameBoard.getNumRows(), gameBoard.getNumCols());
        }

        if (mineHit != null) {
            Dead dead = new Dead(origPosition, mineHit);
            return dead;
        }

        if (finalPos.equals(origPosition)) {
            return new Invalid();
        }

        // Place the player at finalPos (lastEntityCell)
        lastEntityCell.setentity(gameBoard.getPlayer());
        Alive alive = new Alive(origPosition, finalPos);
        alive.setCollectedGems(collectedGems);
        alive.setCollectedExtraLives(collectedExtraLives);
        return alive;
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive)) {
            return;
        }
        Alive alive = (Alive) prevMove;
        Position origPosition = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();

        // Move player back to origPosition
        EntityCell currentOwner = gameBoard.getPlayer().getOwner();
        if (currentOwner != null && !currentOwner.getPosition().equals(origPosition)) {
            currentOwner.setentity(null);
        }

        EntityCell origCell = gameBoard.getEntityCell(origPosition);
        if (origCell != null) {
            origCell.setentity(gameBoard.getPlayer());
        }

        // Restore collected gems
        for (Position gp : alive.getCollectedGems()) {
            EntityCell ec = gameBoard.getEntityCell(gp);
            if (ec != null) {
                ec.setentity(new Gem());
            }
        }

        // Restore collected extra lives
        for (Position lp : alive.getCollectedExtraLives()) {
            EntityCell ec = gameBoard.getEntityCell(lp);
            if (ec != null) {
                ec.setentity(new ExtraLife());
            }
        }

        // Restore cell at newPos if it's a StopCell (we left player there; now leaving empty)
        if (newPos != null && !newPos.equals(origPosition)) {
            EntityCell newCell = gameBoard.getEntityCell(newPos);
            if (newCell instanceof StopCell) {
                ((StopCell) newCell).setentity(null);
            } else if (newCell != null && newCell.getEntity() == gameBoard.getPlayer()) {
                newCell.setentity(null);
            }
        }
    }
}

// =============================================================================
// GameBoardView class (reference implementation verbatim)
// =============================================================================
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

// =============================================================================
// GameState class
// =============================================================================
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
        this.numLives = 0;
        this.initialNumOfGems = 0;
        this.gameBoard = null;
        this.moveStack = new MoveStack();
        this.gameBoardController = null;
        this.gameBoardView = null;
    }

    public GameState(GameBoard gameBoard) {
        this();
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard != null ? gameBoard.getNumGems() : 0;
        this.numLives = UNLIMITED_LIVES;
        if (gameBoard != null) {
            this.gameBoardController = new GameBoardController(gameBoard);
            this.gameBoardView = new GameBoardView(gameBoard);
        }
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this(gameBoard);
        this.numLives = numLives;
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

    public GameBoard getGameBoardField() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        if (gameBoard != null) {
            this.initialNumOfGems = gameBoard.getNumGems();
            this.gameBoardController = new GameBoardController(gameBoard);
            this.gameBoardView = new GameBoardView(gameBoard);
        }
    }

    public MoveStack getMoveStackField() {
        return moveStack;
    }

    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public boolean hasWon() {
        return gameBoard != null && gameBoard.getNumGems() == 0;
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
        return gameBoard != null ? gameBoard.getNumGems() : 0;
    }

    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int initialBoardSize = (gameBoard != null) ? (gameBoard.getNumRows() * gameBoard.getNumCols()) : 0;
        int undoes = (moveStack != null) ? moveStack.getPopCount() : 0;
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        if (gameBoardController == null && gameBoard != null) {
            gameBoardController = new GameBoardController(gameBoard);
        }
        return gameBoardController;
    }

    public GameBoardView getGameBoardView() {
        if (gameBoardView == null && gameBoard != null) {
            gameBoardView = new GameBoardView(gameBoard);
        }
        return gameBoardView;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }
}

// =============================================================================
// GameController class
// =============================================================================
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
        if (gameState == null) {
            return new Invalid();
        }

        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            // Apply collected ExtraLife effects
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(alive);
            return result;
        }

        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            // Dead moves are not pushed
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState == null) {
            return false;
        }
        MoveStack stack = gameState.getMoveStack();
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        MoveResult last = stack.peek();
        if (!(last instanceof Alive)) {
            return false;
        }
        MoveResult popped = stack.pop();
        GameBoardController controller = gameState.getGameBoardController();
        controller.undoMove(popped);
        Alive alive = (Alive) popped;
        // Reverse finite ExtraLife gains
        for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
            gameState.decreaseNumLives(1);
        }
        return true;
    }
}

// =============================================================================
// GameStateSerializer class (reference implementation verbatim)
// =============================================================================
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

// =============================================================================
// FJC class (single-file compilation entry point)
// =============================================================================
class FJC {
    public static void main(String[] args) {
        System.out.println("Inertia game classes compiled successfully.");
    }
}