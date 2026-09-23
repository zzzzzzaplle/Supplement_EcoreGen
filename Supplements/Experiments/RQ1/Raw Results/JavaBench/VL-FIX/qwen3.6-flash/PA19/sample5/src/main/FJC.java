import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reference implementation: Coordinate
 */
class Coordinate {

    public  int row;
    public  int col;

    public Coordinate(int row, int col) {
        this.row = row;
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
 * Reference implementation: Deserializer
 */
class Deserializer {

    
    private Path path;

    public Deserializer(final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    private Deserializer( final Path path) throws FileNotFoundException {
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
     * @param rows Rows of the given map.
     * @param cols Columns of the given map.
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

/**
 * Utility class providing constant characters for rendering map elements.
 */
class PipePatterns {
    // Private constructor to prevent instantiation
    private PipePatterns() {}

    public static final char WALL = '#';

    public static final class Filled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '=';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = 'L';
        public static final char TOP_RIGHT = '7';
        public static final char BOTTOM_LEFT = 'J';
        public static final char BOTTOM_RIGHT = 'F';
        public static final char CROSS = '+';
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = 'L';
        public static final char TOP_RIGHT = '7';
        public static final char BOTTOM_LEFT = 'J';
        public static final char BOTTOM_RIGHT = 'F';
        public static final char CROSS = '+';
    }
}

/**
 * Utility class for string operations.
 */
class StringUtils {
    private StringUtils() {}

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}

/**
 * Interface for elements that can be rendered as a single character.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * Enum representing the four cardinal directions.
 */
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Unknown direction");
        }
    }

    public Coordinate getOffset() {
        switch (this) {
            case UP: return new Coordinate(-1, 0);
            case DOWN: return new Coordinate(1, 0);
            case LEFT: return new Coordinate(0, -1);
            case RIGHT: return new Coordinate(0, 1);
            default: throw new IllegalStateException("Unknown direction");
        }
    }
}

/**
 * Enum representing the type of termination cell.
 */
enum TerminationType {
    SOURCE,
    SINK
}

/**
 * Enum representing the shape of a pipe.
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
 * Abstract base class for all cells in the game map.
 */
abstract class Cell implements MapElement {
    public  Coordinate coord;
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }

    public Cell(Coordinate coord) {
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

    private static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    private static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        return new TerminationCell(coord, type, dir);
    }

    // No-arg constructor required by constraints
    public Cell() {
        this.coord = new Coordinate(0, 0);
    }
}

/**
 * A fillable cell that can hold a pipe.
 */
class FillableCell extends Cell {
    private  Pipe pipe;

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
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
        }
        return '.';
    }

    // No-arg constructor required by constraints
    public FillableCell() {
        super(new Coordinate(0, 0));
        this.pipe = null;
    }
}

/**
 * A termination cell (Source or Sink).
 */
class TerminationCell extends Cell {
    private  boolean isFilled;
    public  Direction pointingTo;
    public  TerminationType type;

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public boolean isFilled() {
        return this.isFilled;
    }

    @Override
    public char toSingleChar() {
        char arrowChar;
        switch (pointingTo) {
            case UP:
                arrowChar = isFilled ? PipePatterns.Filled.UP_ARROW : PipePatterns.Unfilled.UP_ARROW;
                break;
            case DOWN:
                arrowChar = isFilled ? PipePatterns.Filled.DOWN_ARROW : PipePatterns.Unfilled.DOWN_ARROW;
                break;
            case LEFT:
                arrowChar = isFilled ? PipePatterns.Filled.LEFT_ARROW : PipePatterns.Unfilled.LEFT_ARROW;
                break;
            case RIGHT:
                arrowChar = isFilled ? PipePatterns.Filled.RIGHT_ARROW : PipePatterns.Unfilled.RIGHT_ARROW;
                break;
            default:
                throw new IllegalStateException("Invalid pointingTo value!");
        }
        return arrowChar;
    }

    // No-arg constructor required by constraints
    public TerminationCell() {
        super(new Coordinate(0, 0));
        this.type = TerminationType.SINK;
        this.pointingTo = Direction.RIGHT;
        this.isFilled = false;
    }
}

/**
 * Information holder for creating termination cells.
 */
class TerminationCellCreateInfo {
    public  Coordinate coord;
    public  Direction dir;

    public TerminationCellCreateInfo(Coordinate coord, Direction dir) {
        this.coord = coord;
        this.dir = dir;
    }

    // No-arg constructor required by constraints
    public TerminationCellCreateInfo() {
        this.coord = new Coordinate(0, 0);
        this.dir = Direction.RIGHT;
    }
}

/**
 * A wall cell.
 */
class Wall extends Cell {
    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }

    // No-arg constructor required by constraints
    public Wall() {
        super(new Coordinate(0, 0));
    }
}

/**
 * Represents a pipe segment in the game.
 */
class Pipe implements MapElement {
    private  PipeShape shape;
    private  boolean filled;

    public Pipe(PipeShape shape, boolean filled) {
        this.shape = shape;
        this.filled = filled;
    }

    public Pipe() {
        this(PipeShape.HORIZONTAL, false);
    }

    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape shape) { this.shape = shape; }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
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
            case "HZ": return new Pipe(PipeShape.HORIZONTAL, false);
            case "VT": return new Pipe(PipeShape.VERTICAL, false);
            case "TL": return new Pipe(PipeShape.TOP_LEFT, false);
            case "TR": return new Pipe(PipeShape.TOP_RIGHT, false);
            case "BL": return new Pipe(PipeShape.BOTTOM_LEFT, false);
            case "BR": return new Pipe(PipeShape.BOTTOM_RIGHT, false);
            case "CR": return new Pipe(PipeShape.CROSS, false);
            default: throw new IllegalArgumentException("Unknown pipe string: " + rep);
        }
    }
}

/**
 * Represents the game map.
 */
class Map {
    public  int rows;
    public  int cols;
    public  Cell[][] cells;
    private  TerminationCell sourceCell;
    private  TerminationCell sinkCell;
    private  Set<Coordinate> filledTiles;
    private  int prevFilledTiles;
    private  Integer prevFilledDistance;

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;

        // Find source and sink
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        this.sinkCell = tc;
                    }
                }
            }
        }
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        // Check bounds [1..rows-2] x [1..cols-2]
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

        fc.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).setPipe(null);
            }
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        // BFS expansion
        // Current filled tiles are in filledTiles
        Set<Coordinate> nextFilled = new HashSet<>();
        
        // For each filled tile, check its connections
        for (Coordinate c : filledTiles) {
            Cell cell = cells[c.row][c.col];
            
            // If it's a termination cell, check connections
            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                // Only propagate if filled
                if (tc.isFilled()) {
                    Direction dir = tc.pointingTo;
                    Coordinate next = c.add(dir.getOffset());
                    
                    // Check if next is within bounds and is a pipe or sink
                    if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                        Cell nextCell = cells[next.row][next.col];
                        if (nextCell instanceof FillableCell) {
                            FillableCell fc = (FillableCell) nextCell;
                            Optional<Pipe> optPipe = fc.getPipe();
                            if (optPipe.isPresent()) {
                                Pipe p = optPipe.get();
                                Direction[] conns = p.getConnections();
                                // Check if the pipe connects in the opposite direction of where water came from
                                // Water came from 'dir', so we need 'dir.getOpposite()' in the pipe's connections
                                boolean connects = false;
                                for (Direction d : conns) {
                                    if (d == dir.getOpposite()) {
                                        connects = true;
                                        break;
                                    }
                                }
                                if (connects) {
                                    if (!filledTiles.contains(next) && !nextFilled.contains(next)) {
                                        nextFilled.add(next);
                                    }
                                }
                            }
                        } else if (nextCell instanceof TerminationCell) {
                            // Reached sink
                            if (!filledTiles.contains(next) && !nextFilled.contains(next)) {
                                nextFilled.add(next);
                            }
                        }
                    }
                }
            } else if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe p = fc.getPipe().get();
                    if (p.getFilled()) {
                        Direction[] conns = p.getConnections();
                        for (Direction d : conns) {
                            Coordinate next = c.add(d.getOffset());
                            if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                                Cell nextCell = cells[next.row][next.col];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nextFc = (FillableCell) nextCell;
                                    Optional<Pipe> optNextPipe = nextFc.getPipe();
                                    if (optNextPipe.isPresent()) {
                                        Pipe nextP = optNextPipe.get();
                                        Direction[] nextConns = nextP.getConnections();
                                        boolean connects = false;
                                        for (Direction nd : nextConns) {
                                            if (nd == d.getOpposite()) {
                                                connects = true;
                                                break;
                                            }
                                        }
                                        if (connects) {
                                            if (!filledTiles.contains(next) && !nextFilled.contains(next)) {
                                                nextFilled.add(next);
                                            }
                                        }
                                    }
                                } else if (nextCell instanceof TerminationCell) {
                                    if (!filledTiles.contains(next) && !nextFilled.contains(next)) {
                                        nextFilled.add(next);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mark new tiles as filled
        for (Coordinate c : nextFilled) {
            Cell cell = cells[c.row][c.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    fc.getPipe().get().setFilled();
                }
            } else if (cell instanceof TerminationCell) {
                ((TerminationCell) cell).setFilled();
            }
            filledTiles.add(c);
        }

        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        // BFS from source
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        if (sourceCell == null) return false;

        // Start from source, step once in pointingTo direction
        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
            return false;
        }

        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            Cell currCell = cells[curr.row][curr.col];

            // If sink, return true
            if (currCell instanceof TerminationCell && ((TerminationCell) currCell).type == TerminationType.SINK) {
                return true;
            }

            List<Direction> directionsToCheck = new ArrayList<>();

            if (currCell instanceof TerminationCell) {
                directionsToCheck.add(((TerminationCell) currCell).pointingTo);
            } else if (currCell instanceof FillableCell) {
                Optional<Pipe> optPipe = ((FillableCell) currCell).getPipe();
                if (optPipe.isPresent()) {
                    directionsToCheck.addAll(Arrays.asList(optPipe.get().getConnections()));
                }
            }

            for (Direction d : directionsToCheck) {
                Coordinate next = curr.add(d.getOffset());
                if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        // Loss if prevFilledTiles is 0 and distance > 0
        return prevFilledTiles == 0 && prevFilledDistance != null && prevFilledDistance > 0;
    }

    // No-arg constructor required by constraints
    public Map() {
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.sourceCell = null;
        this.sinkCell = null;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;
    }
}

/**
 * Stack for undoing cell placements.
 */
class CellStack {
    private  Stack<FillableCell> cellStack;
    private  int count;

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

    // No-arg constructor required by constraints
    public CellStack(int dummy) {
        this();
    }
}

/**
 * Queue for managing pipes.
 */
class PipeQueue {
    public static final int MAX_GEN_LENGTH = 5;
    private  LinkedList<Pipe> pipeQueue;

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            return generateNewPipe();
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        // Refill if needed
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    public static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        PipeShape randomShape = shapes[(int) (Math.random() * shapes.length)];
        return new Pipe(randomShape, false);
    }

    // No-arg constructor required by constraints
    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }
}

/**
 * Bar for managing delay/timer.
 */
class DelayBar {
    public  int initialValue;
    private  int currentValue;

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void countdown() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public int distance() {
        return -currentValue;
    }

    // No-arg constructor required by constraints
    public DelayBar() {
        this(0);
    }
}

/**
 * The main Game class.
 */
class Game {
    private  int numOfSteps;
    private  Map map;
    private  CellStack cellStack;
    private  PipeQueue pipeQueue;
    private  DelayBar delayBar;

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.numOfSteps = 0;
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char colChar) {
        int col = colChar - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        
        Coordinate coord = new Coordinate(row, col);
        if (map.tryPlacePipe(coord, pipe)) {
            // Find the FillableCell to push to stack
            if (coord.row >= 0 && coord.row < map.rows && coord.col >= 0 && coord.col < map.cols) {
                Cell cell = map.cells[coord.row][coord.col];
                if (cell instanceof FillableCell) {
                    cellStack.push((FillableCell) cell);
                }
            }
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
        
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        
        Coordinate coord = cell.coord;
        map.undo(coord);
        cell.setPipe(null);
        
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar.distance() <= delayBar.initialValue) {
            // Still in delay period
            delayBar.countdown();
        } else {
            // Water flows
            if (delayBar.distance() == delayBar.initialValue + 1) {
                // First round of water flow
                map.fillBeginTile();
            }
            map.fillTiles(delayBar.distance());
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

    // No-arg constructor required by constraints
    public Game() {
        this.numOfSteps = 0;
        this.map = new Map();
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }
}