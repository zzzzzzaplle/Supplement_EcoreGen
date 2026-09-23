import java.util.Objects;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of a coordinate in {Map}.
 */
 class Coordinate {

    public int row;
    public int col;

    public Coordinate() {
        this.row = 0;
        this.col = 0;
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
 * Central constant repository for all map-rendering characters.
 */
final class PipePatterns {
    public static final char WALL = '#';

    public static final class Filled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u2518';
        public static final char TOP_RIGHT = '\u2514';
        public static final char BOTTOM_LEFT = '\u2510';
        public static final char BOTTOM_RIGHT = '\u250C';
        public static final char CROSS = '\u253C';

        private Filled() {}
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u2518';
        public static final char TOP_RIGHT = '\u2514';
        public static final char BOTTOM_LEFT = '\u2510';
        public static final char BOTTOM_RIGHT = '\u250C';
        public static final char CROSS = '\u253C';

        private Unfilled() {}
    }

    private PipePatterns() {}
}

/**
 * Lightweight string helper utilities.
 */
final class StringUtils {
    private StringUtils() {}

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
 * Direction enumeration with opposite and offset.
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
 * Type of termination cell: source or sink.
 */
enum TerminationType {
    SOURCE,
    SINK
}

/**
 * Pipe shape with character representation.
 */
enum PipeShape {
    HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

    private char filledChar;
    private char unfilledChar;

    PipeShape(char filledChar, char unfilledChar) {
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }
}

/**
 * A map element that can be rendered as a single char.
 */
interface MapElement {
    char toSingleChar();
}

/**
 * A delay bar that counts down each round.
 */
class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
        this.initialValue = 0;
        this.currentValue = 0;
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
 * A pipe with shape and fill state.
 */
class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
        this.shape = PipeShape.HORIZONTAL;
        this.filled = false;
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
        this.filled = false;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled() {
        this.filled = true;
    }

    public Direction[] getConnections() {
        switch (shape) {
            case HORIZONTAL: return new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL: return new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT: return new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT: return new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT: return new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT: return new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS: return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            default: throw new IllegalStateException("Unknown shape");
        }
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        if (rep == null) {
            return null;
        }
        switch (rep.trim().toUpperCase()) {
            case "HZ": return new Pipe(PipeShape.HORIZONTAL);
            case "VT": return new Pipe(PipeShape.VERTICAL);
            case "TL": return new Pipe(PipeShape.TOP_LEFT);
            case "TR": return new Pipe(PipeShape.TOP_RIGHT);
            case "BL": return new Pipe(PipeShape.BOTTOM_LEFT);
            case "BR": return new Pipe(PipeShape.BOTTOM_RIGHT);
            case "CR": return new Pipe(PipeShape.CROSS);
            default: return new Pipe(PipeShape.HORIZONTAL);
        }
    }
}

/**
 * Abstract cell with a coordinate.
 */
abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
        this.coord = new Coordinate();
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
        return new FillableCell(coord, pipe);
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        return new TerminationCell(coord, type, dir);
    }
}

/**
 * A fillable cell that may contain a pipe.
 */
class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
        super();
        this.pipe = null;
    }

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
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }
}

/**
 * A termination cell (source or sink) with pointing direction.
 */
class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        super();
        this.isFilled = false;
        this.pointingTo = Direction.UP;
        this.type = TerminationType.SOURCE;
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public boolean getIsFilled() {
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
                case UP: return PipePatterns.Filled.UP_ARROW;
                case DOWN: return PipePatterns.Filled.DOWN_ARROW;
                case LEFT: return PipePatterns.Filled.LEFT_ARROW;
                case RIGHT: return PipePatterns.Filled.RIGHT_ARROW;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP: return PipePatterns.Unfilled.UP_ARROW;
                case DOWN: return PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT: return PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT: return PipePatterns.Unfilled.RIGHT_ARROW;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
    }
}

/**
 * Information for creating a termination cell.
 */
class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
        this.coord = new Coordinate();
        this.dir = Direction.UP;
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
 * A wall cell.
 */
class Wall extends Cell {
    public Wall() {
        super();
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
 * The game map with cells and termination cells.
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
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.sourceCell = null;
        this.sinkCell = null;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
        initTerminationCells();
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
        initTerminationCells();
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

    private void initTerminationCells() {
        this.sourceCell = null;
        this.sinkCell = null;
        if (cells == null) return;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        this.sinkCell = tc;
                    }
                }
            }
        }
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
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
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            ((FillableCell) cell).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (sourceCell == null) return;
        if (distance <= 0) {
            // Only fill the source cell during delay
            sourceCell.setFilled();
            prevFilledTiles = filledTiles.size();
            return;
        }

        // BFS-style expansion
        Queue<Coordinate> queue = new LinkedList<>();
        Queue<Integer> distQueue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        Set<Coordinate> newFilled = new HashSet<>();

        // Start from source cell
        if (sourceCell.getCoord() != null) {
            queue.add(sourceCell.getCoord());
            distQueue.add(0);
            visited.add(sourceCell.getCoord());
            newFilled.add(sourceCell.getCoord());
        }

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            int dist = distQueue.poll();

            if (dist >= distance) continue;

            Cell currentCell = cells[cur.row][cur.col];
            Direction[] dirs = null;
            if (currentCell instanceof FillableCell) {
                Optional<Pipe> p = ((FillableCell) currentCell).getPipe();
                if (p.isPresent()) {
                    dirs = p.get().getConnections();
                }
            } else if (currentCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) currentCell;
                if (tc == sourceCell) {
                    dirs = new Direction[]{tc.getPointingTo()};
                }
            }

            if (dirs == null) continue;

            for (Direction d : dirs) {
                Coordinate next = cur.add(d.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) continue;
                if (visited.contains(next)) continue;
                Cell nc = cells[next.row][next.col];
                if (nc instanceof Wall) continue;
                if (nc instanceof FillableCell) {
                    Optional<Pipe> np = ((FillableCell) nc).getPipe();
                    if (!np.isPresent()) continue;
                    // Check matching connection direction
                    boolean matches = false;
                    for (Direction nd : np.get().getConnections()) {
                        if (nd == d.getOpposite()) {
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) continue;
                    visited.add(next);
                    newFilled.add(next);
                    queue.add(next);
                    distQueue.add(dist + 1);
                    ((FillableCell) nc).getPipe().get().setFilled();
                } else if (nc instanceof TerminationCell) {
                    visited.add(next);
                    newFilled.add(next);
                    queue.add(next);
                    distQueue.add(dist + 1);
                }
            }
        }

        prevFilledTiles = filledTiles.size();
        filledTiles = newFilled;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;
        // BFS from source stepping in source pointingTo direction, then follow pipe connections
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate start = sourceCell.getCoord().add(sourceCell.getPointingTo().getOffset());
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) return false;
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.getCoord())) return true;

            Cell currentCell = cells[cur.row][cur.col];
            Direction[] dirs = null;
            if (currentCell instanceof FillableCell) {
                Optional<Pipe> p = ((FillableCell) currentCell).getPipe();
                if (p.isPresent()) {
                    dirs = p.get().getConnections();
                }
            }

            if (dirs == null) continue;

            for (Direction d : dirs) {
                Coordinate next = cur.add(d.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) continue;
                if (visited.contains(next)) continue;
                Cell nc = cells[next.row][next.col];
                if (nc instanceof Wall) continue;
                if (nc instanceof FillableCell) {
                    Optional<Pipe> np = ((FillableCell) nc).getPipe();
                    if (!np.isPresent()) continue;
                    visited.add(next);
                    queue.add(next);
                } else if (nc instanceof TerminationCell) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }
}

/**
 * Stack of fillable cells for undo.
 */
class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
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

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) return null;
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    public int getUndoCount() {
        return count;
    }
}

/**
 * Queue of pipes with auto-refill.
 */
class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        for (int i = 0; i < MAX_GEN_LENGTH; i++) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            for (Pipe p : pipes) {
                pipeQueue.add(p);
            }
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public static int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }

    public static void setMaxGenLength(int maxGenLength) {
        MAX_GEN_LENGTH = maxGenLength;
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        } else {
            pipeQueue.poll();
        }
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random rand = new Random();
        PipeShape shape = shapes[rand.nextInt(shapes.length)];
        return new Pipe(shape);
    }
}

/**
 * The main game class.
 */
class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.numOfSteps = 0;
        this.map = new Map();
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.numOfSteps = 0;
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
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

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, int col) {
        char columnChar = (char) col;
        int colIndex = columnChar - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) return false;
        Coordinate coord = new Coordinate(row, colIndex);
        boolean placed = map.tryPlacePipe(row, colIndex, pipe);
        if (placed) {
            cellStack.push((FillableCell) map.getCells()[row][colIndex]);
            pipeQueue.consume();
            numOfSteps++;
        }
        return placed;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) return false;
        pipeQueue.undo(cell.getPipe().orElse(null));
        map.undo(cell.getCoord());
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();
        if (distance >= 0) {
            map.fillTiles(distance);
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        if (delayBar.distance() <= 0) {
            return false;
        }
        return map.hasLost();
    }
}