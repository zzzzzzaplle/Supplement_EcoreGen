import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The 2D grid map holding cells, sources and sinks.
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
        this.sourceCell = findSource();
        this.sinkCell = findSink();
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

    private TerminationCell findSource() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        return tc;
                    }
                }
            }
        }
        return null;
    }

    private TerminationCell findSink() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SINK) {
                        return tc;
                    }
                }
            }
        }
        return null;
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
        if (distance < 0) {
            return;
        }
        if (filledTiles.isEmpty()) {
            filledTiles.add(sourceCell.coord);
        }

        int newlyFilled = 0;
        for (int d = 0; d < distance; d++) {
            Set<Coordinate> nextWave = new HashSet<>();
            for (Coordinate filled : filledTiles) {
                Cell cell = cells[filled.row][filled.col];
                if (cell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cell;
                    if (fc.getPipe().isPresent()) {
                        Pipe p = fc.getPipe().get();
                        if (!p.getFilled()) {
                            p.setFilled();
                            newlyFilled++;
                        }
                        for (Direction dir : p.getConnections()) {
                            Coordinate neighbor = filled.add(dir.getOffset());
                            if (neighbor.row < 0 || neighbor.row >= rows
                                    || neighbor.col < 0 || neighbor.col >= cols) {
                                continue;
                            }
                            Cell nc = cells[neighbor.row][neighbor.col];
                            if (nc instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) nc;
                                if (nfc.getPipe().isPresent()) {
                                    Pipe np = nfc.getPipe().get();
                                    boolean matches = false;
                                    for (Direction d2 : np.getConnections()) {
                                        if (d2 == dir.getOpposite()) {
                                            matches = true;
                                            break;
                                        }
                                    }
                                    if (matches) {
                                        nextWave.add(neighbor);
                                    }
                                }
                            } else if (nc instanceof TerminationCell) {
                                TerminationCell tc = (TerminationCell) nc;
                                if (tc.type == TerminationType.SINK
                                        && tc.pointingTo == dir) {
                                    nextWave.add(neighbor);
                                }
                            }
                        }
                    }
                } else if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        Coordinate neighbor = filled.add(tc.pointingTo.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows
                                && neighbor.col >= 0 && neighbor.col < cols) {
                            Cell nc = cells[neighbor.row][neighbor.col];
                            if (nc instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) nc;
                                if (nfc.getPipe().isPresent()) {
                                    Pipe np = nfc.getPipe().get();
                                    boolean matches = false;
                                    for (Direction d2 : np.getConnections()) {
                                        if (d2 == tc.pointingTo.getOpposite()) {
                                            matches = true;
                                            break;
                                        }
                                    }
                                    if (matches) {
                                        nextWave.add(neighbor);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (nextWave.isEmpty()) {
                break;
            }
            for (Coordinate c : nextWave) {
                if (filledTiles.add(c)) {
                    Cell cc = cells[c.row][c.col];
                    if (cc instanceof TerminationCell) {
                        TerminationCell tc = (TerminationCell) cc;
                        if (tc.type == TerminationType.SOURCE) {
                            // skip
                        } else {
                            tc.setFilled();
                            newlyFilled++;
                        }
                    }
                }
            }
        }
        prevFilledTiles = newlyFilled;
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
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
            if (!(cell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) cell;
            if (!fc.getPipe().isPresent()) {
                continue;
            }
            Pipe p = fc.getPipe().get();
            for (Direction dir : p.getConnections()) {
                Coordinate next = cur.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }
                Cell nc = cells[next.row][next.col];
                if (nc instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) nc;
                    if (nfc.getPipe().isPresent()) {
                        Pipe np = nfc.getPipe().get();
                        boolean matches = false;
                        for (Direction d2 : np.getConnections()) {
                            if (d2 == dir.getOpposite()) {
                                matches = true;
                                break;
                            }
                        }
                        if (matches) {
                            visited.add(next);
                            queue.add(next);
                        }
                    }
                } else if (nc instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nc;
                    if (tc == sinkCell && tc.pointingTo == dir) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null
                && prevFilledDistance > 0
                && prevFilledTiles == 0;
    }
}
