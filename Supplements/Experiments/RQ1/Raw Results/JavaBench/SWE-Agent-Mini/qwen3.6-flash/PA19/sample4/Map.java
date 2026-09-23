import java.util.*;

/**
 * Represents the game map with cells, source/sink, and water flow logic.
 */
public class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
        this.filledTiles = new HashSet<>();
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = 0;

        // Find source and sink cells
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

        if (this.sourceCell == null || this.sinkCell == null) {
            throw new IllegalStateException("Map must have both SOURCE and SINK termination cells");
        }
    }

    /**
     * Creates a Map from a string representation.
     */
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Attempts to place a pipe at the given coordinates.
     * Accepts playable-area aligned coordinates (row and col in [1..rows-2]).
     */
    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        // Check bounds for playable area
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];
        // Must be a FillableCell
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        // Cell must not already have a pipe filled in
        FillableCell fCell = (FillableCell) cell;
        if (fCell.getPipe().isPresent()) {
            return false;
        }

        // Place the pipe
        fCell.setPipe(pipe);
        return true;
    }

    /**
     * Undoes placement at the given coordinate.
     */
    public void undo(Coordinate coord) {
        int row = coord.row;
        int col = coord.col;
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            Cell cell = cells[row][col];
            if (cell instanceof FillableCell) {
                FillableCell fCell = (FillableCell) cell;
                if (fCell.getPipe().isPresent()) {
                    fCell.setPipe(null);
                }
            }
        }
    }

    /**
     * Marks the source cell as filled and begins water flow.
     */
    public void fillBeginTile() {
        sourceCell.setFilled();
        filledTiles.add(sourceCell.coord);
    }

    /**
     * Fills pipes within the specified distance from source using BFS.
     */
    public void fillTiles(int distance) {
        Set<Coordinate> newFilled = new HashSet<>();

        for (Coordinate coord : filledTiles) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof TerminationCell && ((TerminationCell) cell).isFilled()) {
                // It's a filled termination cell, follow its pointingTo direction
                TerminationCell tc = (TerminationCell) cell;
                Direction dir = tc.getPointingTo();
                Coordinate next = coord.add(dir.getOffset());
                if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                    Cell nextCell = cells[next.row][next.col];
                    if (nextCell instanceof FillableCell && nextCell instanceof MapElement) {
                        FillableCell fCell = (FillableCell) nextCell;
                        if (fCell.getPipe().isPresent()) {
                            Pipe p = fCell.getPipe().get();
                            if (!p.getFilled()) {
                                Direction opposite = dir.getOpposite();
                                Direction[] conns = p.getConnections();
                                for (Direction conn : conns) {
                                    if (conn == opposite) {
                                        p.setFilled();
                                        newFilled.add(next);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (cell instanceof FillableCell) {
                FillableCell fCell = (FillableCell) cell;
                Optional<Pipe> pipeOpt = fCell.getPipe();
                if (pipeOpt.isPresent()) {
                    Pipe p = pipeOpt.get();
                    if (p.getFilled()) {
                        for (Direction dir : p.getConnections()) {
                            Coordinate next = coord.add(dir.getOffset());
                            if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                                Cell nextCell = cells[next.row][next.col];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nextFCell = (FillableCell) nextCell;
                                    if (nextFCell.getPipe().isPresent()) {
                                        Pipe nextPipe = nextFCell.getPipe().get();
                                        if (!nextPipe.getFilled()) {
                                            Direction opposite = dir.getOpposite();
                                            Direction[] conns = nextPipe.getConnections();
                                            for (Direction conn : conns) {
                                                if (conn == opposite) {
                                                    nextPipe.setFilled();
                                                    newFilled.add(next);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        filledTiles.addAll(newFilled);
        prevFilledTiles = newFilled.size();
        prevFilledDistance = distance;
    }

    /**
     * Uses BFS to check if there is a connected path from SOURCE to SINK.
     * Does not require pipes to be filled, just physically connected.
     */
    public boolean checkPath() {
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start from the SOURCE, step once in its pointingTo direction
        Coordinate start = sourceCell.coord.add(sourceCell.getPointingTo().getOffset());
        if (start.row >= 0 && start.row < rows && start.col >= 0 && start.col < cols) {
            queue.add(start);
            visited.add(start);
        }

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
                FillableCell fCell = (FillableCell) currentCell;
                if (fCell.getPipe().isPresent()) {
                    Pipe p = fCell.getPipe().get();
                    for (Direction dir : p.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                            if (!visited.contains(next)) {
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

    /**
     * Returns true if the game is lost: no new pipes were filled in the previous round
     * and distance > 0 (delay has ended).
     */
    public boolean hasLost() {
        if (prevFilledDistance != null && prevFilledDistance > 0) {
            return prevFilledTiles == 0;
        }
        return false;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }

    public Set<Coordinate> getFilledTiles() {
        return filledTiles;
    }

    public int getPrevFilledTiles() {
        return prevFilledTiles;
    }

    public Integer getPrevFilledDistance() {
        return prevFilledDistance;
    }
}
