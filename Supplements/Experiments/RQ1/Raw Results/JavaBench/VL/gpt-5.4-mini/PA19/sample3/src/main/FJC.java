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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.io.*;

/**
 * Shared rendering constants used by map elements.
 */
class PipePatterns {

    static class Filled {
        static char HORIZONTAL = '=';
        static char VERTICAL = '║';
        static char TOP_LEFT = '╚';
        static char TOP_RIGHT = '╔';
        static char BOTTOM_LEFT = '╗';
        static char BOTTOM_RIGHT = '╝';
        static char CROSS = '╬';
        static char SOURCE_UP = '▲';
        static char SOURCE_DOWN = '▼';
        static char SOURCE_LEFT = '◄';
        static char SOURCE_RIGHT = '►';
        static char SINK_UP = '△';
        static char SINK_DOWN = '▽';
        static char SINK_LEFT = '◁';
        static char SINK_RIGHT = '▷';
    }

    static class Unfilled {
        static char HORIZONTAL = '-';
        static char VERTICAL = '|';
        static char TOP_LEFT = '└';
        static char TOP_RIGHT = '┌';
        static char BOTTOM_LEFT = '┐';
        static char BOTTOM_RIGHT = '┘';
        static char CROSS = '+';
        static char SOURCE_UP = '^';
        static char SOURCE_DOWN = 'v';
        static char SOURCE_LEFT = '<';
        static char SOURCE_RIGHT = '>';
        static char SINK_UP = '^';
        static char SINK_DOWN = 'v';
        static char SINK_LEFT = '<';
        static char SINK_RIGHT = '>';
    }

    static char WALL = '#';
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
        currentValue--;
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

    public static Cell fromChar(char c, Coordinate coord, TerminationType terminationType) {
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

    protected static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    protected static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    protected static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
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

    @Override
    public char toSingleChar() {
        return pipe == null ? '.' : pipe.toSingleChar();
    }

    public Pipe getPipeRaw() {
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

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        if (type == TerminationType.SOURCE) {
            if (pointingTo == Direction.UP) return isFilled ? PipePatterns.Filled.SOURCE_UP : PipePatterns.Unfilled.SOURCE_UP;
            if (pointingTo == Direction.DOWN) return isFilled ? PipePatterns.Filled.SOURCE_DOWN : PipePatterns.Unfilled.SOURCE_DOWN;
            if (pointingTo == Direction.LEFT) return isFilled ? PipePatterns.Filled.SOURCE_LEFT : PipePatterns.Unfilled.SOURCE_LEFT;
            if (pointingTo == Direction.RIGHT) return isFilled ? PipePatterns.Filled.SOURCE_RIGHT : PipePatterns.Unfilled.SOURCE_RIGHT;
        } else if (type == TerminationType.SINK) {
            if (pointingTo == Direction.UP) return isFilled ? PipePatterns.Filled.SINK_UP : PipePatterns.Unfilled.SINK_UP;
            if (pointingTo == Direction.DOWN) return isFilled ? PipePatterns.Filled.SINK_DOWN : PipePatterns.Unfilled.SINK_DOWN;
            if (pointingTo == Direction.LEFT) return isFilled ? PipePatterns.Filled.SINK_LEFT : PipePatterns.Unfilled.SINK_LEFT;
            if (pointingTo == Direction.RIGHT) return isFilled ? PipePatterns.Filled.SINK_RIGHT : PipePatterns.Unfilled.SINK_RIGHT;
        }
        throw new IllegalStateException("Unknown termination state");
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean filled) {
        isFilled = filled;
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

    @Override
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
        filled = true;
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
                throw new IllegalStateException("Unknown pipe shape");
        }
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        if (rep == null) return null;
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
                shape = PipeShape.HORIZONTAL;
        }
        return new Pipe(shape);
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
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
        if (cellStack.isEmpty()) return null;
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

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) pipeQueue.removeFirst();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) pipeQueue.addFirst(pipe);
        while (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] values = PipeShape.values();
        return new Pipe(values[(int) (Math.random() * values.length)]);
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
        this.filledTiles = new HashSet<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) sourceCell = tc;
                    if (tc.type == TerminationType.SINK) sinkCell = tc;
                }
            }
        }
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) return false;
        if (!(cells[row][col] instanceof FillableCell)) return false;
        FillableCell fc = (FillableCell) cells[row][col];
        if (fc.getPipe().isPresent()) return false;
        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord == null) return;
        if (cells[coord.row][coord.col] instanceof FillableCell) {
            ((FillableCell) cells[coord.row][coord.col]).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (sourceCell == null) return;
        if (distance < 0) return;
        if (distance == 0) {
            fillBeginTile();
            return;
        }
        Deque<Coordinate> q = new ArrayDeque<>();
        Set<Coordinate> seen = new HashSet<>();
        q.add(sourceCell.coord);
        seen.add(sourceCell.coord);
        while (!q.isEmpty()) {
            Coordinate cur = q.removeFirst();
            Cell cell = cells[cur.row][cur.col];
            if (cell instanceof TerminationCell) {
                ((TerminationCell) cell).setFilled();
            } else if (cell instanceof FillableCell) {
                Pipe pipe = ((FillableCell) cell).getPipeRaw();
                if (pipe != null) pipe.setFilled(true);
            }
            if (cur.equals(sourceCell.coord)) {
                for (Direction d : new Direction[]{sourceCell.pointingTo}) {
                    Coordinate nxt = cur.add(d.getOffset());
                    if (nxt.row >= 0 && nxt.row < rows && nxt.col >= 0 && nxt.col < cols && seen.add(nxt)) q.add(nxt);
                }
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;
        Coordinate cur = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        Set<Coordinate> seen = new HashSet<>();
        while (cur.row >= 0 && cur.row < rows && cur.col >= 0 && cur.col < cols && seen.add(cur)) {
            Cell cell = cells[cur.row][cur.col];
            if (cell == sinkCell) return true;
            if (!(cell instanceof FillableCell)) return false;
            Pipe pipe = ((FillableCell) cell).getPipeRaw();
            if (pipe == null) return false;
            boolean moved = false;
            for (Direction d : pipe.getConnections()) {
                Coordinate nxt = cur.add(d.getOffset());
                if (nxt.row >= 0 && nxt.row < rows && nxt.col >= 0 && nxt.col < cols) {
                    Cell nextCell = cells[nxt.row][nxt.col];
                    if (nextCell == sinkCell) {
                        if (d == sinkCell.pointingTo.getOpposite()) return true;
                    } else if (nextCell instanceof FillableCell) {
                        Pipe np = ((FillableCell) nextCell).getPipeRaw();
                        if (np != null) {
                            for (Direction nd : np.getConnections()) {
                                if (nd == d.getOpposite()) {
                                    cur = nxt;
                                    moved = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (moved) break;
            }
            if (!moved) return false;
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null && prevFilledDistance > 0 && prevFilledTiles == filledTiles.size();
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
        Pipe p = pipeQueue.peek();
        if (p == null) return false;
        boolean ok = map.tryPlacePipe(row, internalCol, p);
        if (ok) {
            Cell target = map.cells[row][internalCol];
            if (target instanceof FillableCell) {
                cellStack.push((FillableCell) target);
            }
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
        if (cell == null) return false;
        Pipe restored = cell.getPipeRaw();
        cell.setPipe(null);
        pipeQueue.undo(restored);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar.getCurrentValue() > 0) {
            delayBar.countdown();
            return;
        }
        map.fillBeginTile();
        map.setPrevFilledTiles(map.getFilledTiles().size());
        map.setPrevFilledDistance(delayBar.distance());
        map.fillTiles(delayBar.distance());
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

class StringUtils {
    public StringUtils() {
    }

    public static String repeat(char ch, int count) {
        if (count == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String pad(char ch, int count) {
        return repeat(ch, count);
    }
}

class Deserializer {
    private Path path;

    public Deserializer(String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    private Deserializer(Path path) throws FileNotFoundException {
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
                defaultPipes = Arrays.stream(s.split(",")).map(Pipe::fromString).collect(Collectors.toList());
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