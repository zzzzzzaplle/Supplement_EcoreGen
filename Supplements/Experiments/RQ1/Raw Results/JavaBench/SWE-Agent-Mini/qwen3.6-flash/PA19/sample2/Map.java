import java.util.*;

public class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
        rows = 0;
        cols = 0;
        cells = new Cell[0][0];
        filledTiles = new HashSet();
        prevFilledTiles = 0;
        prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        filledTiles = new HashSet();
        prevFilledTiles = 0;
        prevFilledDistance = null;
    }

    public boolean fillTile(Coordinate coord) {
        if (!isValid(coord)) {
            return false;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            if (fc.getPipe().isPresent()) {
                fc.getPipe().get().setFilled();
                filledTiles.add(coord);
                return true;
            }
        }
        return false;
    }

    public boolean isFilled(Coordinate coord) {
        return filledTiles != null && filledTiles.contains(coord);
    }

    public boolean isFilled(int row, int col) {
        return isValid(new Coordinate(row, col)) && filledTiles.contains(new Coordinate(row, col));
    }


    public int getFilledTilesCount() {
        filledTiles.size();
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
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
        fc.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        if (isValid(coord)) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    fc.setPipe(null);
                }
            }
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }
        filledTiles.add(sourceCell);
        Map distMap = new HashMap();
        distMap.put(sourceCell, new Integer(0));
        List queue = new ArrayList();
        queue.add(sourceCell);
        for (int i = 0; i < distance; i++) {
            List nextQ = new ArrayList();
            for (Object o : queue) {
                Coordinate c = (Coordinate) o;
                if (c instanceof FillableCell) {
                }
            }
            queue = nextQ;
        }
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Map visited = new HashMap();
        List queue = new ArrayList();
        Coordinate start = new Coordinate(sourceCell.coord.row + sourceCell.pointingTo.getOffset().row, sourceCell.coord.col + sourceCell.pointingTo.getOffset().col);
        if (!isValid(start)) {
            return false;
        }
        Cell startCell = cells[start.row][start.col];
        if (startCell instanceof TerminationCell) {
            return ((TerminationCell) startCell).type == TerminationType.SINK;
        }
        if (!(startCell instanceof FillableCell)) {
            return false;
        }
        FillableCell fcStart = (FillableCell) startCell;
        if (!fcStart.getPipe().isPresent()) {
            return false;
        }
        visited.put(start, new Integer(0));
        queue.add(start);
        while (!queue.isEmpty()) {
            Coordinate current = (Coordinate) queue.get(0);
            queue.remove(0);
            Cell cell = cells[current.row][current.col];
            if (cell instanceof TerminationCell) {
                return ((TerminationCell) cell).type == TerminationType.SINK;
            }
            if (!(cell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) cell;
            if (!fc.getPipe().isPresent()) {
                continue;
            }
            Direction[] dirs = fc.getPipe().get().getConnections();
            for (int i = 0; i < dirs.length; i++) {
                Coordinate next = new Coordinate(current.row + dirs[i].getOffset().row, current.col + dirs[i].getOffset().col);
                if (!isValid(next) || visited.containsKey(next)) {
                    continue;
                }
                Cell nextCell = cells[next.row][next.col];
                if (nextCell instanceof TerminationCell) {
                    return ((TerminationCell) nextCell).type == TerminationType.SINK;
                }
                if (nextCell instanceof FillableCell) {
                    FillableCell fcN = (FillableCell) nextCell;
                    if (!fcN.getPipe().isPresent()) {
                        continue;
                    }
                    Direction[] nDirs = fcN.getPipe().get().getConnections();
                    for (int j = 0; j < nDirs.length; j++) {
                        if (nDirs[j] == dirs[i]) {
                            visited.put(next, new Integer(0));
                            queue.add(next);
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }

    private boolean isValid(Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
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

    public Set getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set filledTiles) {
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
