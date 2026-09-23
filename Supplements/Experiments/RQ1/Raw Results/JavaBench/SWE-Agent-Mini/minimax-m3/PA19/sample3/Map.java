import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The game map containing the cells, source, sink, and water flow logic.
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
        this.cells = new Cell[0][0];
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
        locateTerminations();
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

    private void locateTerminations() {
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

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (cells == null) {
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

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public void undo(Coordinate coord) {
        if (cells == null) {
            return;
        }
        if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
            return;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            ((FillableCell) cell).clearPipe();
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (sourceCell == null) {
            return;
        }
        int newlyFilled = 0;
        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (!isInBounds(start.row, start.col)) {
            return;
        }
        Cell startCell = cells[start.row][start.col];
        if (!(startCell instanceof FillableCell)) {
            return;
        }
        FillableCell fc = (FillableCell) startCell;
        if (fc.getPipe().isPresent()) {
            Pipe p = fc.getPipe().get();
            p.setFilled();
            filledTiles.add(start);
            newlyFilled++;
            expandFill(fc, distance - 1, newlyFilled);
        }
        prevFilledTiles = newlyFilled;
        prevFilledDistance = distance;
    }

    private void expandFill(FillableCell origin, int remainingDistance, int newlyFilled) {
        if (remainingDistance < 0 || origin == null) {
            return;
        }
        Pipe p = origin.getPipe().orElse(null);
        if (p == null) {
            return;
        }
        Direction[] dirs = p.getConnections();
        for (Direction d : dirs) {
            Coordinate next = origin.coord.add(d.getOffset());
            if (!isInBounds(next.row, next.col)) {
                continue;
            }
            Cell cell = cells[next.row][next.col];
            if (cell instanceof FillableCell) {
                FillableCell nfc = (FillableCell) cell;
                if (nfc.getPipe().isPresent() && !filledTiles.contains(next)) {
                    Pipe np = nfc.getPipe().get();
                    Direction[] ndirs = np.getConnections();
                    boolean match = false;
                    for (Direction nd : ndirs) {
                        if (nd == d.getOpposite()) {
                            match = true;
                            break;
                        }
                    }
                    if (match) {
                        np.setFilled();
                        filledTiles.add(next);
                        newlyFilled++;
                        if (remainingDistance > 0) {
                            expandFill(nfc, remainingDistance - 1, newlyFilled);
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    tc.setFilled();
                    filledTiles.add(next);
                }
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (!isInBounds(start.row, start.col)) {
            return false;
        }
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[cur.row][cur.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (!fc.getPipe().isPresent()) {
                    continue;
                }
                Direction[] dirs = fc.getPipe().get().getConnections();
                for (Direction d : dirs) {
                    Coordinate next = cur.add(d.getOffset());
                    if (!isInBounds(next.row, next.col)) {
                        continue;
                    }
                    if (visited.contains(next)) {
                        continue;
                    }
                    Cell ncell = cells[next.row][next.col];
                    if (ncell instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) ncell;
                        if (nfc.getPipe().isPresent()) {
                            Direction[] ndirs = nfc.getPipe().get().getConnections();
                            for (Direction nd : ndirs) {
                                if (nd == d.getOpposite()) {
                                    visited.add(next);
                                    queue.add(next);
                                    break;
                                }
                            }
                        }
                    } else if (ncell instanceof TerminationCell) {
                        TerminationCell tc = (TerminationCell) ncell;
                        if (tc.getType() == TerminationType.SINK) {
                            visited.add(next);
                            queue.add(next);
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}
