import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.io.*;

/**
 * Utility class for pipe game rendering characters.
 */
class PipePatterns {
    static class Filled {
        static char HORIZONTAL = '-';
        static char VERTICAL = '|';
        static char TOP_LEFT = '┌';
        static char TOP_RIGHT = '┐';
        static char BOTTOM_LEFT = '└';
        static char BOTTOM_RIGHT = '┘';
        static char CROSS = '+';
    }

    static class Unfilled {
        static char HORIZONTAL = '=';
        static char VERTICAL = '‖';
        static char TOP_LEFT = 'a';
        static char TOP_RIGHT = 'b';
        static char BOTTOM_LEFT = 'c';
        static char BOTTOM_RIGHT = 'd';
        static char CROSS = 'x';
    }

    static char WALL = '#';
    static char FILLED_SOURCE_UP = '^';
    static char FILLED_SOURCE_DOWN = 'v';
    static char FILLED_SOURCE_LEFT = '<';
    static char FILLED_SOURCE_RIGHT = '>';
    static char UNFILLED_SOURCE_UP = 'A';
    static char UNFILLED_SOURCE_DOWN = 'V';
    static char UNFILLED_SOURCE_LEFT = 'L';
    static char UNFILLED_SOURCE_RIGHT = 'R';

    static char FILLED_SINK_UP = 'U';
    static char FILLED_SINK_DOWN = 'D';
    static char FILLED_SINK_LEFT = 'F';
    static char FILLED_SINK_RIGHT = 'G';
    static char UNFILLED_SINK_UP = 'u';
    static char UNFILLED_SINK_DOWN = 'd';
    static char UNFILLED_SINK_LEFT = 'l';
    static char UNFILLED_SINK_RIGHT = 'r';
}

/**
 * Lightweight string helpers.
 */
class StringUtils {
    public StringUtils() {
    }

    static String pad(char ch, int count) {
        if (count == 0) {
            return "";
        }
        return String.valueOf(ch).repeat(count);
    }
}

interface MapElement {
    char toSingleChar();
}

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

enum TerminationType {
    SOURCE,
    SINK
}

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

    PipeShape() {
    }

    PipeShape(char filledChar, char unfilledChar) {
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }
}

class Coordinate {
    public int row;
    public int col;

    public Coordinate() {
    }

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

class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void countdown() {
        if (currentValue > Integer.MIN_VALUE) {
            currentValue--;
        }
    }

    public int distance() {
        return Math.max(0, initialValue - currentValue);
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
}

abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
    }

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

    static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }
}

class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
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

    public char toSingleChar() {
        return pipe == null ? '.' : pipe.toSingleChar();
    }

    public Pipe getPipeDirect() {
        return pipe;
    }
}

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

    public void setFilled() {
        this.isFilled = true;
    }

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public char toSingleChar() {
        boolean filled = isFilled;
        if (type == TerminationType.SOURCE) {
            switch (pointingTo) {
                case UP:
                    return filled ? PipePatterns.FILLED_SOURCE_UP : PipePatterns.UNFILLED_SOURCE_UP;
                case DOWN:
                    return filled ? PipePatterns.FILLED_SOURCE_DOWN : PipePatterns.UNFILLED_SOURCE_DOWN;
                case LEFT:
                    return filled ? PipePatterns.FILLED_SOURCE_LEFT : PipePatterns.UNFILLED_SOURCE_LEFT;
                case RIGHT:
                    return filled ? PipePatterns.FILLED_SOURCE_RIGHT : PipePatterns.UNFILLED_SOURCE_RIGHT;
                default:
                    throw new IllegalStateException("Unknown direction");
            }
        }
        switch (pointingTo) {
            case UP:
                return filled ? PipePatterns.FILLED_SINK_UP : PipePatterns.UNFILLED_SINK_UP;
            case DOWN:
                return filled ? PipePatterns.FILLED_SINK_DOWN : PipePatterns.UNFILLED_SINK_DOWN;
            case LEFT:
                return filled ? PipePatterns.FILLED_SINK_LEFT : PipePatterns.UNFILLED_SINK_LEFT;
            case RIGHT:
                return filled ? PipePatterns.FILLED_SINK_RIGHT : PipePatterns.UNFILLED_SINK_RIGHT;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }
}

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

class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

class Pipe implements MapElement {
    private PipeShape shape;
    private boolean filled;

    public Pipe() {
    }

    public Pipe(PipeShape shape) {
        this.shape = shape;
    }

    public void setFilled() {
        this.filled = true;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
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

    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        String s = rep.trim().toUpperCase();
        PipeShape shape;
        switch (s) {
            case "HZ":
                shape = PipeShape.HORIZONTAL;
                break;
            case "VT":
                shape = PipeShape.VERTICAL;
                break;
            case "TL":
                shape = PipeShape.TOP_LEFT;
                break;
            case "TR":
                shape = PipeShape.TOP_RIGHT;
                break;
            case "BL":
                shape = PipeShape.BOTTOM_LEFT;
                break;
            case "BR":
                shape = PipeShape.BOTTOM_RIGHT;
                break;
            case "CR":
                shape = PipeShape.CROSS;
                break;
            default:
                throw new IllegalArgumentException("Unknown pipe code: " + rep);
        }
        return new Pipe(shape);
    }
}

class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
        count++;
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        return cellStack.pop();
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

    public void setCount(int count) {
        this.count = count;
    }
}

class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this();
        if (pipes != null) {
            pipeQueue.addAll(pipes);
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
            while (pipeQueue.size() < MAX_GEN_LENGTH) {
                pipeQueue.add(generateNewPipe());
            }
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
            while (pipeQueue.size() > MAX_GEN_LENGTH) {
                pipeQueue.removeLast();
            }
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        return new Pipe(shapes[(int) (Math.random() * shapes.length)]);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}

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
        this.filledTiles = new java.util.HashSet<>();
        locateTerminationCells();
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fillable = (FillableCell) cell;
        if (fillable.getPipe().isPresent()) {
            return false;
        }
        fillable.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord == null) return;
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
        fillBeginTile();
        Queue<Coordinate> q = new ArrayDeque<>();
        Queue<Integer> d = new ArrayDeque<>();
        q.add(sourceCell.coord);
        d.add(0);
        filledTiles.clear();
        filledTiles.add(sourceCell.coord);
        while (!q.isEmpty()) {
            Coordinate cur = q.remove();
            int dist = d.remove();
            if (dist >= distance) continue;
            Cell c = cells[cur.row][cur.col];
            if (c instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) c;
                for (Direction dir : new Direction[]{tc.getPointingTo()}) {
                    Coordinate next = cur.add(dir.getOffset());
                    if (inBounds(next) && canTraverseFrom(c, dir, next)) {
                        markFilled(next);
                        q.add(next);
                        d.add(dist + 1);
                    }
                }
            } else if (c instanceof FillableCell) {
                Pipe pipe = ((FillableCell) c).getPipeDirect();
                if (pipe == null) continue;
                for (Direction dir : pipe.getConnections()) {
                    Coordinate next = cur.add(dir.getOffset());
                    if (inBounds(next) && canTraverseFrom(c, dir, next)) {
                        markFilled(next);
                        q.add(next);
                        d.add(dist + 1);
                    }
                }
            }
        }
    }

    private boolean canTraverseFrom(Cell from, Direction dir, Coordinate next) {
        Cell n = cells[next.row][next.col];
        Direction opposite = dir.getOpposite();
        if (from instanceof TerminationCell) {
            if (n instanceof FillableCell) {
                Pipe p = ((FillableCell) n).getPipeDirect();
                return p != null && containsDir(p.getConnections(), opposite);
            }
            return false;
        }
        if (from instanceof FillableCell) {
            Pipe p = ((FillableCell) from).getPipeDirect();
            if (p == null) return false;
            if (n instanceof TerminationCell) {
                return ((TerminationCell) n).getPointingTo() == opposite;
            }
            if (n instanceof FillableCell) {
                Pipe np = ((FillableCell) n).getPipeDirect();
                return np != null && containsDir(np.getConnections(), opposite);
            }
        }
        return false;
    }

    private boolean containsDir(Direction[] dirs, Direction dir) {
        for (Direction d : dirs) {
            if (d == dir) return true;
        }
        return false;
    }

    private void markFilled(Coordinate c) {
        Cell cell = cells[c.row][c.col];
        if (cell instanceof FillableCell) {
            Pipe p = ((FillableCell) cell).getPipeDirect();
            if (p != null) p.setFilled();
        } else if (cell instanceof TerminationCell) {
            ((TerminationCell) cell).setFilled();
        }
        filledTiles.add(c);
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;
        java.util.HashSet<Coordinate> visited = new java.util.HashSet<>();
        Coordinate start = sourceCell.coord.add(sourceCell.getPointingTo().getOffset());
        return dfsPath(start, sourceCell.getPointingTo(), visited);
    }

    private boolean dfsPath(Coordinate coord, Direction fromDir, Set<Coordinate> visited) {
        if (!inBounds(coord) || visited.contains(coord)) return false;
        visited.add(coord);
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof TerminationCell) {
            return cell == sinkCell;
        }
        if (!(cell instanceof FillableCell)) return false;
        Pipe pipe = ((FillableCell) cell).getPipeDirect();
        if (pipe == null) return false;
        for (Direction dir : pipe.getConnections()) {
            if (dir == fromDir.getOpposite()) continue;
            Coordinate next = coord.add(dir.getOffset());
            if (dfsPath(next, dir, visited)) return true;
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null && prevFilledDistance > 0 && prevFilledTiles == filledTiles.size();
    }

    private boolean inBounds(Coordinate c) {
        return c.row >= 0 && c.row < rows && c.col >= 0 && c.col < cols;
    }

    private void locateTerminationCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.getType() == TerminationType.SOURCE) sourceCell = tc;
                    if (tc.getType() == TerminationType.SINK) sinkCell = tc;
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
}

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

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, int col) {
        return placePipe(row, (char) col);
    }

    public boolean placePipe(int row, char col) {
        int internalCol = Character.toUpperCase(col) - 'A' + 1;
        Pipe front = pipeQueue.peek();
        boolean ok = map.tryPlacePipe(row, internalCol, front);
        if (ok) {
            cellStack.push((FillableCell) map.cells[row][internalCol]);
            pipeQueue.consume();
            numOfSteps++;
        }
        return ok;
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
        Pipe pipe = cell.getPipeDirect();
        map.undo(cell.getCoord());
        pipeQueue.undo(pipe);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        if (delayBar.getCurrentValue() > 0) {
            return;
        }
        map.fillTiles(delayBar.distance());
        map.setPrevFilledTiles(map.getFilledTiles().size());
        map.setPrevFilledDistance(delayBar.distance());
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

    public int getNumOfStepsValue() {
        return numOfSteps;
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

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }
}

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