import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// --- Utility Classes ---

final class PipePatterns {
    private PipePatterns() {}
    public static final char WALL = '#';
    public static class Filled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '═', VERTICAL = '║', TOP_LEFT = '╔', TOP_RIGHT = '╗', BOTTOM_LEFT = '╚', BOTTOM_RIGHT = '╝', CROSS = '╬';
    }
    public static class Unfilled {
        public static final char UP_ARROW = '^', DOWN_ARROW = 'v', LEFT_ARROW = '<', RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-', VERTICAL = '|', TOP_LEFT = '┌', TOP_RIGHT = '┐', BOTTOM_LEFT = '└', BOTTOM_RIGHT = '┘', CROSS = '+';
    }
}

final class StringUtils {
    private StringUtils() {}
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(Math.max(0, count));
    }
}

// --- Enums ---

enum Direction {
    UP, DOWN, LEFT, RIGHT;
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
    public Coordinate getOffset() {
        return switch (this) {
            case UP -> new Coordinate(-1, 0);
            case DOWN -> new Coordinate(1, 0);
            case LEFT -> new Coordinate(0, -1);
            case RIGHT -> new Coordinate(0, 1);
        };
    }
}

enum TerminationType { SOURCE, SINK }

enum PipeShape {
    HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

    private final char filled;
    private final char unfilled;
    PipeShape(char filled, char unfilled) { this.filled = filled; this.unfilled = unfilled; }
    public char getCharByState(boolean isFilled) { return isFilled ? filled : unfilled; }
}

// --- Core Classes ---

interface MapElement { char toSingleChar(); }

class Coordinate {
    public int row, col;
    public Coordinate(int row, int col) { this.row = row; this.col = col; }
    public boolean equals(Object obj) { return obj instanceof Coordinate other && row == other.row && col == other.col; }
    public Coordinate add(Coordinate other) { return new Coordinate(this.row + other.row, this.col + other.col); }
    @Override public int hashCode() { return Objects.hash(row, col); }
}

class DelayBar {
    private int initialValue, currentValue;
    public DelayBar() {}
    public void countdown() { if (currentValue > 0) currentValue--; }
    public int distance() { return -currentValue; }
    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int v) { this.initialValue = v; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int v) { this.currentValue = v; }
}

abstract class Cell implements MapElement {
    public Coordinate coord;
    public Cell() {}
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }
    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        return switch (c) {
            case 'W' -> new Wall(coord);
            case '.' -> new FillableCell(coord, null);
            case '^' -> new TerminationCell(coord, terminationType, Direction.UP);
            case '>' -> new TerminationCell(coord, terminationType, Direction.RIGHT);
            case '<' -> new TerminationCell(coord, terminationType, Direction.LEFT);
            case 'v' -> new TerminationCell(coord, terminationType, Direction.DOWN);
            default -> null;
        };
    }
    protected static Wall createWall(Coordinate coord) { return new Wall(coord); }
    protected static FillableCell createFillableCell(Coordinate coord, Pipe p) { return new FillableCell(coord, p); }
    protected static TerminationCell createTerminationCell(Coordinate coord, TerminationType t, Direction d) { return new TerminationCell(coord, t, d); }
}

class FillableCell extends Cell {
    private Pipe pipe;
    public FillableCell() {}
    public FillableCell(Coordinate c, Pipe p) { this.coord = c; this.pipe = p; }
    public Optional<Pipe> getPipe() { return Optional.ofNullable(pipe); }
    public void setPipe(Pipe pipe) { this.pipe = pipe; }
    public char toSingleChar() { return pipe != null ? pipe.toSingleChar() : '.'; }
}

class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;
    public TerminationCell() {}
    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord; this.type = type; this.pointingTo = pointingTo;
    }
    public void setFilled() { this.isFilled = true; }
    public boolean getIsFilled() { return isFilled; }
    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction d) { this.pointingTo = d; }
    public TerminationType getType() { return type; }
    public void setType(TerminationType t) { this.type = t; }
    public char toSingleChar() {
        char[][] map = isFilled ? new char[][]{{PipePatterns.Filled.UP_ARROW, PipePatterns.Filled.DOWN_ARROW, PipePatterns.Filled.LEFT_ARROW, PipePatterns.Filled.RIGHT_ARROW}}
                                : new char[][]{{PipePatterns.Unfilled.UP_ARROW, PipePatterns.Unfilled.DOWN_ARROW, PipePatterns.Unfilled.LEFT_ARROW, PipePatterns.Unfilled.RIGHT_ARROW}};
        return switch(pointingTo) {
            case UP -> map[0][0]; case DOWN -> map[0][1]; case LEFT -> map[0][2]; case RIGHT -> map[0][3];
        };
    }
}

class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;
    public TerminationCellCreateInfo() {}
}

class Wall extends Cell {
    public Wall(Coordinate c) { this.coord = c; }
    public Wall() {}
    public char toSingleChar() { return PipePatterns.WALL; }
}

class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;
    public Pipe() {}
    public void setFilled(boolean f) { this.filled = f; }
    public boolean getFilled() { return filled; }
    public PipeShape getShape() { return shape; }
    public void setShape(PipeShape s) { this.shape = s; }
    public Direction[] getConnections() { throw new UnsupportedOperationException(); }
    public char toSingleChar() { return shape.getCharByState(filled); }
    public static Pipe fromString(String rep) { return new Pipe(); }
}

class Map {
    private int rows, cols, prevFilledTiles;
    public Cell[][] cells;
    private TerminationCell sourceCell, sinkCell;
    private Set<Coordinate> filledTiles;
    private Integer prevFilledDistance;
    public Map() {}
    public Map(int r, int c, Cell[][] cl) { this.rows = r; this.cols = c; this.cells = cl; }
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) { return tryPlacePipe(coord.row, coord.col, pipe); }
    public boolean tryPlacePipe(int r, int c, Pipe p) { return true; }
    public void undo(Coordinate c) {}
    public void fillBeginTile() { sourceCell.setFilled(); }
    public void fillTiles(int d) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return prevFilledTiles == 0; }
}

class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count;
    public void push(FillableCell c) { cellStack.push(c); }
    public FillableCell pop() { count++; return cellStack.pop(); }
    public int getUndoCount() { return count; }
}

class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue = new LinkedList<>();
    public PipeQueue(List<Pipe> pipes) { pipeQueue.addAll(pipes); }
    public Pipe peek() { return pipeQueue.peek(); }
    public void consume() { pipeQueue.poll(); }
    public void undo(Pipe p) { pipeQueue.addFirst(p); }
    private static Pipe generateNewPipe() { return new Pipe(); }
}

class Game {
    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    public Game() {}
    public Game(int r, int c, int d, Cell[][] cl, List<Pipe> p) {}
    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }
    public boolean placePipe(int row, char col) { return true; }
    public void skipPipe() { numOfSteps++; }
    public boolean undoStep() { return false; }
    public void updateState() {}
    public boolean hasWon() { return false; }
    public boolean hasLost() { return map.hasLost(); }
    public int getNumOfSteps() { return numOfSteps; }
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