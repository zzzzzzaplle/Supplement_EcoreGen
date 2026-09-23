import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE && sourceCell == null) {
                        this.sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK && sinkCell == null) {
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

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
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
        if (fc.getRawPipe() != null) {
            return false;
        }
        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (cells == null) {
            return;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            fc.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (cells == null) {
            return;
        }
        Set<Coordinate> filled = new HashSet<>();
        if (sourceCell != null && sourceCell.isFilled()) {
            filled.add(sourceCell.coord);
        }
        int newlyFilledCount = 0;
        for (int step = 0; step < distance; step++) {
            Set<Coordinate> nextFilled = new HashSet<>();
            for (Coordinate c : filled) {
                Cell cell = cells[c.row][c.col];
                if (!(cell instanceof FillableCell)) {
                    continue;
                }
                FillableCell fc = (FillableCell) cell;
                Pipe pipe = fc.getRawPipe();
                if (pipe == null) {
                    continue;
                }
                for (Direction dir : pipe.getConnections()) {
                    Coordinate neighbor = c.add(dir.getOffset());
                    if (!isInBounds(neighbor)) {
                        continue;
                    }
                    if (filled.contains(neighbor)) {
                        continue;
                    }
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof FillableCell) {
                        FillableCell nfc = (FillableCell) neighborCell;
                        Pipe np = nfc.getRawPipe();
                        if (np == null) {
                            continue;
                        }
                        for (Direction ndir : np.getConnections()) {
                            if (ndir == dir.getOpposite()) {
                                np.setFilled();
                                nextFilled.add(neighbor);
                                break;
                            }
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        if (neighborCell == sinkCell) {
                            ((TerminationCell) neighborCell).setFilled();
                            nextFilled.add(neighbor);
                        }
                    }
                }
            }
            newlyFilledCount += nextFilled.size();
            filled.addAll(nextFilled);
        }
        this.prevFilledTiles = newlyFilledCount;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        if (cells == null) {
            return false;
        }
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (!isInBounds(start)) {
            return false;
        }
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate c = queue.poll();
            if (c.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[c.row][c.col];
            if (!(cell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) cell;
            Pipe pipe = fc.getRawPipe();
            if (pipe == null) {
                continue;
            }
            for (Direction dir : pipe.getConnections()) {
                Coordinate next = c.add(dir.getOffset());
                if (!isInBounds(next) || visited.contains(next)) {
                    continue;
                }
                Cell nextCell = cells[next.row][next.col];
                if (nextCell instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) nextCell;
                    Pipe np = nfc.getRawPipe();
                    if (np == null) {
                        continue;
                    }
                    for (Direction ndir : np.getConnections()) {
                        if (ndir == dir.getOpposite()) {
                            visited.add(next);
                            queue.add(next);
                            break;
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    if (nextCell == sinkCell) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }

    private boolean isInBounds(Coordinate c) {
        return c.row >= 0 && c.row < rows && c.col >= 0 && c.col < cols;
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
