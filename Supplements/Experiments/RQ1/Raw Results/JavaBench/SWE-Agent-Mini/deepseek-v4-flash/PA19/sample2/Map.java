import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the game map with a 2D grid of cells.
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
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
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

    /**
     * Tries to place a pipe at the given coordinate.
     *
     * @param coord the coordinate
     * @param pipe  the pipe to place
     * @return true if placement succeeded, false otherwise
     */
    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at the given row and column.
     *
     * @param row the row (playable area aligned, 1-based)
     * @param col the column (playable area aligned, 1-based)
     * @param p   the pipe to place
     * @return true if placement succeeded, false otherwise
     */
    public boolean tryPlacePipe(int row, int col, Pipe p) {
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

    /**
     * Undoes a pipe placement at the given coordinate.
     *
     * @param coord the coordinate to clear
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
     * Marks the source cell as filled.
     */
    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    /**
     * Fills pipes within the specified distance from the source using BFS expansion.
     *
     * @param distance the distance to fill
     */
    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        prevFilledDistance = distance;
        prevFilledTiles = 0;

        // BFS from source
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        int[] dist = new int[rows * cols];

        // Start from the cell in the direction the source is pointing
        Coordinate sourceOffset = sourceCell.pointingTo.getOffset();
        Coordinate startCoord = sourceCell.coord.add(sourceOffset);

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return;
        }

        queue.add(startCoord);
        visited.add(startCoord);
        dist[startCoord.row * cols + startCoord.col] = 1;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = dist[current.row * cols + current.col];

            if (currentDist > distance) {
                continue;
            }

            Cell cell = cells[current.row][current.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    if (!pipe.getFilled()) {
                        pipe.setFilled();
                        filledTiles.add(current);
                        prevFilledTiles++;
                    }

                    // Explore connections
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols
                                && !visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) neighborCell;
                                if (nfc.getPipe().isPresent()) {
                                    Pipe neighborPipe = nfc.getPipe().get();
                                    // Check if connection directions match
                                    Direction opposite = dir.getOpposite();
                                    boolean match = false;
                                    for (Direction nd : neighborPipe.getConnections()) {
                                        if (nd == opposite) {
                                            match = true;
                                            break;
                                        }
                                    }
                                    if (match) {
                                        visited.add(neighbor);
                                        dist[neighbor.row * cols + neighbor.col] = currentDist + 1;
                                        queue.add(neighbor);
                                    }
                                }
                            } else if (neighborCell instanceof TerminationCell) {
                                // Check if we reached sink
                                TerminationCell tc = (TerminationCell) neighborCell;
                                if (tc.type == TerminationType.SINK) {
                                    visited.add(neighbor);
                                    dist[neighbor.row * cols + neighbor.col] = currentDist + 1;
                                    queue.add(neighbor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if a connected path exists from SOURCE to SINK.
     * Does not require pipes to be filled.
     *
     * @return true if a path exists
     */
    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate sourceOffset = sourceCell.pointingTo.getOffset();
        Coordinate startCoord = sourceCell.coord.add(sourceOffset);

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            return false;
        }

        queue.add(startCoord);
        visited.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];

            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.type == TerminationType.SINK) {
                    return true;
                }
            }

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols
                                && !visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) neighborCell;
                                if (nfc.getPipe().isPresent()) {
                                    Pipe neighborPipe = nfc.getPipe().get();
                                    Direction opposite = dir.getOpposite();
                                    boolean match = false;
                                    for (Direction nd : neighborPipe.getConnections()) {
                                        if (nd == opposite) {
                                            match = true;
                                            break;
                                        }
                                    }
                                    if (match) {
                                        visited.add(neighbor);
                                        queue.add(neighbor);
                                    }
                                }
                            } else if (neighborCell instanceof TerminationCell) {
                                visited.add(neighbor);
                                queue.add(neighbor);
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
     *
     * @return true if the player has lost
     */
    public boolean hasLost() {
        return prevFilledDistance != null && prevFilledDistance > 0 && prevFilledTiles == 0;
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
