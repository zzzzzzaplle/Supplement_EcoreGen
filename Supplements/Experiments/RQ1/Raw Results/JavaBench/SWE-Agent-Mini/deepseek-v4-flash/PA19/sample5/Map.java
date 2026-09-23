import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the game map containing cells.
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
        this.rows = 0;
        this.cols = 0;
        this.cells = null;
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

        // Find source and sink termination cells
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

    /**
     * Creates a Map from string representation.
     */
    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
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

    public int getPrevFilledTiles() {
        return prevFilledTiles;
    }

    public Integer getPrevFilledDistance() {
        return prevFilledDistance;
    }

    /**
     * Attempts to place a pipe at the given coordinate.
     */
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Attempts to place a pipe at the given row and column.
     * Accepts playable-area aligned coordinates (row and col in [1..rows-2] x [1..cols-2]).
     */
    public boolean tryPlacePipe(int row, int col, Pipe p) {
        // Check bounds: must be within playable area
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];

        // Must be a FillableCell
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fillableCell = (FillableCell) cell;

        // Must not already have a pipe
        if (fillableCell.getPipe().isPresent()) {
            return false;
        }

        fillableCell.setPipe(p);
        return true;
    }

    /**
     * Undoes a pipe placement at the given coordinate.
     */
    public void undo(Coordinate coord) {
        if (coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).setPipe(null);
            }
        }
    }

    /**
     * Marks the source cell as filled (begins water flow).
     */
    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    /**
     * Fills pipes within the specified distance from the source using BFS.
     */
    public void fillTiles(int distance) {
        // If distance is 0 or negative, no filling occurs
        if (distance <= 0) {
            prevFilledTiles = 0;
            prevFilledDistance = distance;
            return;
        }

        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start BFS from source
        Coordinate sourceCoord = sourceCell.coord;
        Coordinate startCoord = sourceCoord.add(sourceCell.pointingTo.getOffset());
        queue.add(startCoord);

        // Track visited tiles and distances
        Set<Coordinate> visited = new HashSet<>();
        visited.add(sourceCoord);
        java.util.Map<Coordinate, Integer> distMap = new java.util.HashMap<>();
        distMap.put(startCoord, 1);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distMap.get(current);

            if (currentDist > distance) {
                continue;
            }

            // Check if this is a playable cell with a pipe
            if (current.row >= 1 && current.row < rows - 1 && current.col >= 1 && current.col < cols - 1) {
                Cell cell = cells[current.row][current.col];
                if (cell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cell;
                    if (fc.getPipe().isPresent()) {
                        Pipe pipe = fc.getPipe().get();
                        pipe.setFilled();
                        newFilled.add(current);

                        // Explore connections
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate next = current.add(dir.getOffset());

                            // Check bounds
                            if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                                if (!visited.contains(next)) {
                                    visited.add(next);

                                    // Check if there's a matching connection from the neighbor
                                    Cell neighborCell = cells[next.row][next.col];
                                    if (neighborCell instanceof FillableCell) {
                                        FillableCell nfc = (FillableCell) neighborCell;
                                        if (nfc.getPipe().isPresent()) {
                                            Pipe neighborPipe = nfc.getPipe().get();
                                            Direction opposite = dir.getOpposite();
                                            boolean hasMatching = false;
                                            for (Direction nd : neighborPipe.getConnections()) {
                                                if (nd == opposite) {
                                                    hasMatching = true;
                                                    break;
                                                }
                                            }
                                            if (hasMatching) {
                                                distMap.put(next, currentDist + 1);
                                                queue.add(next);
                                            }
                                        }
                                    } else if (neighborCell instanceof TerminationCell) {
                                        // Could be sink
                                        distMap.put(next, currentDist + 1);
                                        queue.add(next);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        prevFilledTiles = newFilled.size();
        prevFilledDistance = distance;
        filledTiles.addAll(newFilled);
    }

    /**
     * Checks if a connected path exists from SOURCE to SINK using BFS.
     * Does not require pipes to be filled.
     *
     * @return true if a path exists, false otherwise
     */
    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Start from source, step once in the source's pointing direction
        Coordinate sourceCoord = sourceCell.coord;
        Coordinate startCoord = sourceCoord.add(sourceCell.pointingTo.getOffset());
        visited.add(sourceCoord);
        queue.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            // Check bounds
            if (current.row < 0 || current.row >= rows || current.col < 0 || current.col >= cols) {
                continue;
            }

            // Check if we reached the sink
            Cell cell = cells[current.row][current.col];
            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.type == TerminationType.SINK) {
                    return true;
                }
            }

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // If it's a FillableCell with a pipe, follow its connections
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        if (!visited.contains(next)) {
                            // Check neighbor has matching connection
                            if (next.row >= 0 && next.row < rows && next.col >= 0 && next.col < cols) {
                                Cell neighborCell = cells[next.row][next.col];
                                if (neighborCell instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) neighborCell;
                                    if (nfc.getPipe().isPresent()) {
                                        Pipe neighborPipe = nfc.getPipe().get();
                                        Direction opposite = dir.getOpposite();
                                        boolean hasMatching = false;
                                        for (Direction nd : neighborPipe.getConnections()) {
                                            if (nd == opposite) {
                                                hasMatching = true;
                                                break;
                                            }
                                        }
                                        if (hasMatching) {
                                            queue.add(next);
                                        }
                                    }
                                } else if (neighborCell instanceof TerminationCell) {
                                    queue.add(next);
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns true if no new pipes were filled in the previous round (loss condition).
     * Game checks loss only after delay has ended (distance > 0).
     *
     * @return true if the player has lost
     */
    public boolean hasLost() {
        return prevFilledTiles == 0;
    }
}
