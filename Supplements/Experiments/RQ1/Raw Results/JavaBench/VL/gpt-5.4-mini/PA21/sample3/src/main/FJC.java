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

interface BoardElement {
    char toUnicodeChar();

    char toASCIIChar();
}

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
            case LEFT, RIGHT -> 0;
        };
    }

    public int getColOffset() {
        return switch (this) {
            case LEFT -> -1;
            case RIGHT -> 1;
            case UP, DOWN -> 0;
        };
    }
}

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
        this.owner = ownner;
        return ownner;
    }
}

class EntityCell extends Cell {
    Entity entity;

    public EntityCell() {
        super();
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
        locatePlayer();
    }

    public GameBoard(int numRows, int numCols, Cell[][] board, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = player;
    }

    private void locatePlayer() {
        if (board == null) return;
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    this.player = p;
                    return;
                }
            }
        }
        this.player = new Player();
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
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    count++;
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        return (EntityCell) board[r][c];
    }

    public EntityCell getEntityCell(Position position) {
        return (EntityCell) board[position.getRow()][position.getCol()];
    }
}

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
        Position start = gameBoard.getPlayer().getPosition();
        if (start == null) {
            return new Invalid();
        }
        Position current = start;
        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        boolean moved = false;

        while (true) {
            Position next = current.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(), gameBoard.getNumCols());
            if (next == null) {
                break;
            }
            Cell nextCell = gameBoard.getCell(next.getRow(), next.getCol());
            if (nextCell instanceof Wall) {
                break;
            }
            moved = true;
            if (nextCell instanceof StopCell) {
                current = next;
                break;
            }
            if (nextCell instanceof EntityCell ec) {
                Entity entity = ec.getEntity();
                if (entity instanceof Mine) {
                    return new Dead(start, next);
                }
                if (entity instanceof Gem) {
                    collectedGems.add(next);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(next);
                }
            }
            current = next;
        }

        if (!moved || current.equals(start)) {
            return new Invalid();
        }
        return new Alive(start, current, collectedGems, collectedExtraLives);
    }

    public void undoMove(MoveResult prevMove) {
        if (!(prevMove instanceof Alive alive)) {
            return;
        }
        Position orig = alive.getOrigPosition();
        Position newPos = alive.getNewPosition();
        if (orig != null && newPos != null) {
            Cell origCell = gameBoard.getCell(orig.getRow(), orig.getCol());
            Cell newCell = gameBoard.getCell(newPos.getRow(), newPos.getCol());
            if (origCell instanceof EntityCell oec && newCell instanceof EntityCell nec) {
                Entity player = nec.getEntity();
                nec.setEntity(null);
                oec.setEntity(player);
                if (player instanceof Player p) {
                    gameBoard.setPlayer(p);
                    p.setPosition(orig);
                }
            }
        }
        for (Position p : alive.getCollectedGems()) {
            Cell cell = gameBoard.getCell(p.getRow(), p.getCol());
            if (cell instanceof EntityCell ec) ec.setEntity(new Gem());
        }
        for (Position p : alive.getCollectedExtraLives()) {
            Cell cell = gameBoard.getCell(p.getRow(), p.getCol());
            if (cell instanceof EntityCell ec) ec.setEntity(new ExtraLife());
        }
    }
}

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
        if (result instanceof Alive alive) {
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
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
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive alive) {
            for (Position p : alive.getCollectedExtraLives()) {
                if (!gameState.hasUnlimitedLives()) {
                    gameState.decreaseNumLives(1);
                }
            }
            gameState.getGameBoardController().undoMove(alive);
            return true;
        }
        return false;
    }
}

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
    }

    public GameState(GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard != null ? gameBoard.getNumGems() : 0;
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
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
        return gameBoard != null ? gameBoard.getNumGems() : 0;
    }

    public int getScore() {
        int boardSize = gameBoard != null ? gameBoard.getNumRows() * gameBoard.getNumCols() : 0;
        int collectedGems = initialNumOfGems - getNumGems();
        int undoes = moveStack != null ? moveStack.getPopCount() : 0;
        return boardSize + (collectedGems * 10) - (numMoves) - (undoes * 2) - (numDeaths * 4);
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
}

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

class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
    }

    public Valid(Position origPosition, Position newPosition) {
        this.origPosition = origPosition;
        this.newPosition = newPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}

class Alive extends Valid {
    public List<Position> collectedGems;
    public List<Position> collectedExtraLives;

    public Alive() {
        this.collectedGems = new ArrayList<>();
        this.collectedExtraLives = new ArrayList<>();
    }

    public Alive(Position origPosition, Position newPosition, List<Position> collectedGems, List<Position> collectedExtraLives) {
        super(origPosition, newPosition);
        this.collectedGems = collectedGems;
        this.collectedExtraLives = collectedExtraLives;
    }

    public List<Position> getCollectedGems() {
        if (collectedGems == null) collectedGems = new ArrayList<>();
        return collectedGems;
    }

    public void setCollectedGems(List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public List<Position> getCollectedExtraLives() {
        if (collectedExtraLives == null) collectedExtraLives = new ArrayList<>();
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}

class Dead extends Valid {
    public Position minePosition;

    public Dead() {
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

class Invalid extends MoveResult {
    public Invalid() {
    }
}

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position other)) return false;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

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

class InertiaTextGame {
    public InertiaTextGame() {
    }

    public static void main(String[] args) throws Exception {
        // Minimal runner placeholder consistent with the model.
        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
        GameState gameState = null;
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.equalsIgnoreCase("Quit")) {
                break;
            }
            if (line.equalsIgnoreCase("Undo") && gameState != null) {
                GameController controller = new GameController(gameState);
                controller.processUndo();
                continue;
            }
            if (gameState != null) {
                GameController controller = new GameController(gameState);
                switch (line.toUpperCase()) {
                    case "U" -> controller.processMove(Direction.UP);
                    case "D" -> controller.processMove(Direction.DOWN);
                    case "L" -> controller.processMove(Direction.LEFT);
                    case "R" -> controller.processMove(Direction.RIGHT);
                    default -> {
                    }
                }
            }
        }
    }
}