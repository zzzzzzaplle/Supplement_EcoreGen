import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The game map containing cells, source and sink, and water flow logic.
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
        this.sourceCell = null;
        this.sinkCell = null;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
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
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cells[row][col];
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
            return;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            ((FillableCell) cell).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        if (sourceCell != null) {
            queue.add(sourceCell.coord);
            newFilled.add(sourceCell.coord);
        }
        int currentDist = 0;
        while (!queue.isEmpty() && currentDist < distance) {
            int size = queue.size();
            for (int i = 0; i < size; ++i) {
                Coordinate cur = queue.poll();
                Cell cell = cells[cur.row][cur.col];
                if (!(cell instanceof FillableCell)) {
                    continue;
                }
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isEmpty()) {
                    continue;
                }
                Pipe pipe = fc.getPipe().get();
                for (Direction d : pipe.getConnections()) {
                    Coordinate neighbor = cur.add(d.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    Cell nCell = cells[neighbor.row][neighbor.col];
                    if (newFilled.contains(neighbor)) {
                        continue;
                    }
                    if (nCell instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) nCell;
                        if (nfc.getPipe().isPresent()) {
                            Pipe nPipe = nfc.getPipe().get();
                            boolean connected = false;
                            for (Direction nd : nPipe.getConnections()) {
                                if (nd == d.getOpposite()) {
                                    connected = true;
                                    break;
                                }
                            }
                            if (connected) {
                                newFilled.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    } else if (nCell == sinkCell) {
                        newFilled.add(neighbor);
                    }
                }
            }
            currentDist++;
        }
        // mark filled pipes
        for (Coordinate c : newFilled) {
            Cell cell = cells[c.row][c.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    fc.getPipe().get().setFilled();
                }
            }
        }
        prevFilledTiles = newFilled.size() - (sourceCell != null ? 1 : 0);
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
        if (start.equals(sinkCell.coord)) {
            return true;
        }
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            Cell cell = cells[cur.row][cur.col];
            if (!(cell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) cell;
            if (fc.getPipe().isEmpty()) {
                continue;
            }
            Pipe pipe = fc.getPipe().get();
            for (Direction d : pipe.getConnections()) {
                Coordinate neighbor = cur.add(d.getOffset());
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                if (visited.contains(neighbor)) {
                    continue;
                }
                if (neighbor.equals(sinkCell.coord)) {
                    return true;
                }
                Cell nCell = cells[neighbor.row][neighbor.col];
                if (nCell instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) nCell;
                    if (nfc.getPipe().isPresent()) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0 && prevFilledDistance != null && prevFilledDistance > 0;
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
