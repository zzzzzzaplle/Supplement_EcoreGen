import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * Representation of a coordinate in {Map}.
 */
 class Coordinate {

    public int row;
    public int col;

    public Coordinate() {
    }

    public Coordinate(int row, int col) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }

        return equals((Coordinate) obj);
    }

    public boolean equals(Coordinate other) {
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(this.row + other.row, this.col + other.col);
    }
}

/**
 * Representation of a direction in the game.
 */
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }

    public Coordinate getOffset() {
        switch (this) {
            case UP:
                return new Coordinate(-1, 0);
            case DOWN:
                return new Coordinate(1, 0);
            case LEFT:
                return new Coordinate(0, -1);
            case RIGHT:
                return new Coordinate(0, 1);
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }
}

/**
 * Enum representing termination types.
 */
enum TerminationType {
    SOURCE,
    SINK
}

/**
 * Enum representing pipe shapes.
 */
enum PipeShape {
    HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

    private final char filledChar;
    private final char unfilledChar;

    PipeShape(char filledChar, char unfilledChar) {
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }
}

/**
 * Interface for map elements.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * Abstract class representing a cell on the map.
 */
abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
    }

    public Cell(Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W':
                return createWall(coord);
            case '.':
                return createFillableCell(coord, null);
            case '^':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.UP);
            case '>':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.RIGHT);
            case '<':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.DOWN);
            default:
                return null;
        }
    }

    private static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell(coord);
        cell.setPipe(pipe);
        return cell;
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}

/**
 * Class representing a fillable cell that can hold a pipe.
 */
class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord) {
        super(coord);
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        } else {
            return '.';
        }
    }
}

/**
 * Class representing a termination cell (source or sink).
 */
class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public Direction getPointingTo() {
        return pointingTo;
    }

    public void setPointingTo(Direction pointingTo) {
        this.pointingTo = pointingTo;
    }

    public TerminationType getType() {
        return type;
    }

    public void setType(TerminationType type) {
        this.type = type;
    }

    @Override
    public char toSingleChar() {
        if (isFilled) {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Filled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Filled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Filled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Filled.RIGHT_ARROW;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Unfilled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Unfilled.RIGHT_ARROW;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
    }
}

/**
 * Helper class for creating termination cells.
 */
class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
    }

    public TerminationCellCreateInfo(Coordinate coord, Direction dir) {
        this.coord = coord;
        this.dir = dir;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }
}

/**
 * Class representing a wall cell.
 */
class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

/**
 * Class representing a pipe.
 */
class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL:
                return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL:
                return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT:
                return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS:
                return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default:
                throw new IllegalStateException("Unknown shape");
        }
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        switch (rep) {
            case "HZ":
                return new Pipe(PipeShape.HORIZONTAL);
            case "VT":
                return new Pipe(PipeShape.VERTICAL);
            case "TL":
                return new Pipe(PipeShape.TOP_LEFT);
            case "TR":
                return new Pipe(PipeShape.TOP_RIGHT);
            case "BL":
                return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR":
                return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR":
                return new Pipe(PipeShape.CROSS);
            default:
                throw new IllegalArgumentException("Unknown pipe representation: " + rep);
        }
    }
}

/**
 * Class representing the game map.
 */
class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;

        // Find source and sink
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public void setSourceCell(TerminationCell sourceCell) {
        this.sourceCell = sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }

    public void setSinkCell(TerminationCell sinkCell) {
        this.sinkCell = sinkCell;
    }

    public Set<Coordinate> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<Coordinate> filledTiles) {
        this.filledTiles = filledTiles;
    }

    public int getPrevFilledTiles() {
        return prevFilledTiles;
    }

    public void setPrevFilledTiles(int prevFilledTiles) {
        this.prevFilledTiles = prevFilledTiles;
    }

    public Integer getPrevFilledDistance() {
        return prevFilledDistance;
    }

    public void setPrevFilledDistance(Integer prevFilledDistance) {
        this.prevFilledDistance = prevFilledDistance;
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        // Check bounds: playable area is [1..rows-2] x [1..cols-2]
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fc = (FillableCell) cell;
        if (fc.getPipe().isPresent()) {
            return false;
        }

        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            ((FillableCell) cell).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        // BFS from source
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start from source's pointingTo direction
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        queue.add(startCoord);
        visited.add(startCoord);

        int currentDistance = 0;
        int newFilled = 0;

        while (!queue.isEmpty() && currentDistance < distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                Cell currentCell = cells[current.row][current.col];

                if (currentCell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) currentCell;
                    if (fc.getPipe().isPresent()) {
                        Pipe pipe = fc.getPipe().get();
                        if (!pipe.getFilled()) {
                            pipe.setFilled();
                            filledTiles.add(current);
                            newFilled++;
                        }

                        // Add connected cells
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate next = current.add(dir.getOffset());
                            if (!visited.contains(next) && next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                                Cell nextCell = cells[next.row][next.col];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nextFc = (FillableCell) nextCell;
                                    if (nextFc.getPipe().isPresent()) {
                                        Pipe nextPipe = nextFc.getPipe().get();
                                        // Check if directions match
                                        if (Arrays.asList(nextPipe.getConnections()).contains(dir.getOpposite())) {
                                            visited.add(next);
                                            queue.add(next);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentDistance++;
        }

        prevFilledTiles = newFilled;
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        // BFS to check if path exists from source to sink
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start from source's pointingTo direction
        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return false;
        }

        queue.add(startCoord);
        visited.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell currentCell = cells[current.row][current.col];

            if (currentCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) currentCell;
                if (tc.type == TerminationType.SINK) {
                    return true;
                }
            }

            if (currentCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) currentCell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        if (!visited.contains(next) && next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                            Cell nextCell = cells[next.row][next.col];
                            if (nextCell instanceof FillableCell) {
                                FillableCell nextFc = (FillableCell) nextCell;
                                if (nextFc.getPipe().isPresent()) {
                                    Pipe nextPipe = nextFc.getPipe().get();
                                    if (Arrays.asList(nextPipe.getConnections()).contains(dir.getOpposite())) {
                                        visited.add(next);
                                        queue.add(next);
                                    }
                                }
                            } else if (nextCell instanceof TerminationCell) {
                                visited.add(next);
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0 && prevFilledDistance != null && prevFilledDistance > 0;
    }
}

/**
 * Class representing a stack of cells for undo functionality.
 */
class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        if (!cellStack.isEmpty()) {
            count++;
            return cellStack.pop();
        }
        return null;
    }

    public int getUndoCount() {
        return count;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

/**
 * Class representing a queue of pipes.
 */
class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        // Auto-refill to MAX_GEN_LENGTH if needed
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.addLast(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.pollFirst();
            // Auto-refill
            while (pipeQueue.size() < MAX_GEN_LENGTH) {
                pipeQueue.addLast(generateNewPipe());
            }
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        int randomIndex = new Random().nextInt(shapes.length);
        return new Pipe(shapes[randomIndex]);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}

/**
 * Class representing a delay bar for water flow timing.
 */
class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void countdown() {
        currentValue--;
    }

    public int distance() {
        return -currentValue;
    }
}

/**
 * Main game class.
 */
class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }

        boolean success = map.tryPlacePipe(row, colIndex, currentPipe);
        if (success) {
            FillableCell cell = (FillableCell) map.cells[row][colIndex];
            cellStack.push(cell);
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }

        Coordinate coord = cell.getCoord();
        Pipe removedPipe = cell.getPipe().orElse(null);
        if (removedPipe != null) {
            pipeQueue.undo(removedPipe);
        }
        map.undo(coord);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();
        if (distance >= 0) {
            map.fillBeginTile();
            map.fillTiles(distance);
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        return map.hasLost();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public CellStack getCellStack() {
        return cellStack;
    }

    public void setCellStack(CellStack cellStack) {
        this.cellStack = cellStack;
    }

    public PipeQueue getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(PipeQueue pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public DelayBar getDelayBar() {
        return delayBar;
    }

    public void setDelayBar(DelayBar delayBar) {
        this.delayBar = delayBar;
    }
}

/**
 * Utility class for pipe pattern characters.
 */
final class PipePatterns {
    public static final char WALL = '█';

    private PipePatterns() {
    }

    public static final class Filled {
        public static final char UP_ARROW = '↑';
        public static final char DOWN_ARROW = '↓';
        public static final char LEFT_ARROW = '←';
        public static final char RIGHT_ARROW = '→';
        public static final char HORIZONTAL = '━';
        public static final char VERTICAL = '┃';
        public static final char TOP_LEFT = '┛';
        public static final char TOP_RIGHT = '┗';
        public static final char BOTTOM_LEFT = '┓';
        public static final char BOTTOM_RIGHT = '┏';
        public static final char CROSS = '╋';

        private Filled() {
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '─';
        public static final char VERTICAL = '│';
        public static final char TOP_LEFT = '┘';
        public static final char TOP_RIGHT = '└';
        public static final char BOTTOM_LEFT = '┐';
        public static final char BOTTOM_RIGHT = '┌';
        public static final char CROSS = '┼';

        private Unfilled() {
        }
    }
}

/**
 * Utility class for string operations.
 */
final class StringUtils {
    private StringUtils() {
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}


/**
 * A deserializer for converting a map file into a {@link Game}.
 */
 class Deserializer {

    private Path path;

    public Deserializer(final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    private Deserializer(final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }

    public Game parseGame() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            var rows = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                rows = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var cols = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                cols = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var delay = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                delay = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            final var mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) {
                line = getFirstNonEmptyLine(reader);
                if (line == null) {
                    throw new EOFException();
                }

                mapRep.add(line);
            }
            final var cells = parseString(rows, cols, String.join("\n", mapRep));

            List<Pipe> defaultPipes = null;
            String s = getFirstNonEmptyLine(reader);
            if (s != null) {
                defaultPipes = Arrays.stream(s.split(","))
                        .map(Pipe::fromString)
                        .collect(Collectors.toList());
            }

            return new Game(rows, cols, delay, cells, defaultPipes);
        } catch (EOFException eofe) {
            System.err.println("Unexpected EOF");
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Deserializes a map from a {@link java.lang.String}.
     *
     * @param rows    Rows of the given map.
     * @param cols    Columns of the given map.
     * @param cellsRep String representation of the map, with rows delimited by {@code '\n'}.
     * @return A 2D cell array from the string. Note that this cell array may not fully conform to the requirements of
     * an actual game map; The "map conformance" checks are performed in the {@link Map} constructor.
     */
    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);

                Cell cell;
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cell = Cell.fromChar(ch, coord, TerminationType.SINK);
                } else {
                    cell = Cell.fromChar(ch, coord, TerminationType.SOURCE);
                }

                cells[r][c] = cell;
            }
        }

        return cells;
    }

    private String getFirstNonEmptyLine(final BufferedReader br) throws IOException {
        do {
            String s = br.readLine();

            if (s == null) {
                return null;
            }
            if (s.isBlank() || s.startsWith("#")) {
                continue;
            }

            return s;
        } while (true);
    }
}