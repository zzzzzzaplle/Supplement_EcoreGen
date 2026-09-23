import java.util.*;
public class Map {
    private int rows, cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {}
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.getType() == TerminationType.SOURCE) sourceCell = tc;
                    else sinkCell = tc;
                }
            }
        }
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
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
        if (cells[coord.row][coord.col] instanceof FillableCell) {
            ((FillableCell) cells[coord.row][coord.col]).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return false; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
    public Cell[][] getCells() { return cells; }
    public void setCells(Cell[][] cells) { this.cells = cells; }
}
