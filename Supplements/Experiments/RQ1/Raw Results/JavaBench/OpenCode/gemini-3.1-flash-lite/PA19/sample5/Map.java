import java.util.Set;

public class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {}

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
    public Cell[][] getCells() { return cells; }
    public void setCells(Cell[][] cells) { this.cells = cells; }
    public TerminationCell getSourceCell() { return sourceCell; }
    public void setSourceCell(TerminationCell sourceCell) { this.sourceCell = sourceCell; }
    public TerminationCell getSinkCell() { return sinkCell; }
    public void setSinkCell(TerminationCell sinkCell) { this.sinkCell = sinkCell; }
    public Set getFilledTiles() { return filledTiles; }
    public void setFilledTiles(Set filledTiles) { this.filledTiles = filledTiles; }
    public int getPrevFilledTiles() { return prevFilledTiles; }
    public void setPrevFilledTiles(int prevFilledTiles) { this.prevFilledTiles = prevFilledTiles; }
    public Integer getPrevFilledDistance() { return prevFilledDistance; }
    public void setPrevFilledDistance(Integer prevFilledDistance) { this.prevFilledDistance = prevFilledDistance; }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) { return false; }
    public void undo(Coordinate coord) {}
    public void fillBeginTile() { sourceCell.setFilled(); }
    public void fillTiles(int distance) {}
    public boolean checkPath() { return false; }
    public boolean hasLost() { return false; }
}
